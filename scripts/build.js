var
    options = {
        gufo: {
            iri: "${gufo.iri}",
            localFile: "${basedir}/src/general/owl/gufo.ttl"
        },
        gufoIris: {
            ufoTreeFile: "${basedir}/scripts/ufo-tree.txt",
            templateSource: "${basedir}/src/template/java/GufoIris.java"
        },
        generated: {
            sources: "${js.generated.sources}",
            resources: "${js.generated.resources}"
        },
        validation: {
            package: "${validation.main.package}",
            rulesPackage: "${validation.rules.package}",
            ruleList: "${validation.rules.resource.name}",
        }
    },

    // Info on IRIS
    gufoIris = {
        generate: false,
        classIRIs: [],
        publicClassNames: [],
        templateFile: null,
        ufoTreeFile: null,
        targetFile: null,
        prefixes: [],
        nsToPrefix: {},
        tree: [],
        treeClassNames: {}
    },

    // Making Netbeans happy
    java, Packages, arguments,
    File = java.io.File
    ;

function fileFromPath(path) {
    return new File(path.replace(/\//g, File.separator));
}

function fileWriter(file) {
    var
        outputStream, outputWriter,
        result
        ;
    if (!(file instanceof File)) {
        file = fileFromPath(file);
    }
    file.parentFile.mkdirs();
    outputStream = new java.io.FileOutputStream(file);
    outputWriter = new java.io.OutputStreamWriter(outputStream);

    return result = {
        append: (data) => (outputWriter.append(data), result),
        close: ()=> (outputWriter.close(), outputStream.close())
    };
}

function fileLines(file) {
    var
        inputStream, inputReader, lineNumberReader, lines
        ;

    function close() {
        lineNumberReader.close();
        inputReader.close();
        inputStream.close();
    }

    if (!(file instanceof File)) {
        file = fileFromPath(file);
    }
    inputStream = new java.io.FileInputStream(file);
    inputReader = new java.io.InputStreamReader(inputStream);
    lineNumberReader = new java.io.LineNumberReader(inputReader);
    lines = lineNumberReader.lines();

    return {
        forEach: (consumer) => {
            lines.forEach(line => consumer(line)); close();
        },
        anyMatch: (predicate) => {
            lines.anyMatch(line => Boolean(predicate(line))); close();
        }
    };
}

(steps => steps.forEach(step => (print(step.name), step())))([

/**
 * Creates some file objects that are goind to be used in next steps.
 */
function createBaseFileObjects() {
    gufoIris.templateFile = fileFromPath(options.gufoIris.templateSource);
    gufoIris.ufoTreeFile = fileFromPath(options.gufoIris.ufoTreeFile);
},

/**
 * Generates the GufoIris class file object and compares relevant file
 * modification dates to GufoIris class file modification date to decide whether
 * the class file generation should take place.
 */
function generateGufoIrisClassFileObject() {
    var
        packageRegExp = /\s*package\s+([A-Za-z_.]+)\s*;/,
        package, classFileName,
        match
        ;
    fileLines(gufoIris.templateFile)
        .anyMatch(line => match = packageRegExp.exec(line));
    package = match[1];
    classFileName = gufoIris.templateFile.name;
    gufoIris.targetFile = fileFromPath(
        [ options.generated.sources ]
        .concat( package.split("."), [ classFileName ] )
        .join("/")
    );
    gufoIris.generate =
        (
            !gufoIris.targetFile.exists()
        ) || (
            gufoIris.targetFile.lastModified() <
            gufoIris.templateFile.lastModified()
        ) || (
            gufoIris.targetFile.lastModified() <
            gufoIris.ufoTreeFile.lastModified()
        );
},

/**
 * This step populates the array gufoIris.classIRIs with the IRI's found in
 * local file holding the gUFO ontology.
 */
function generateGufoIriClasses() {
    var
        ontologyManager,
        gufo, gufoLocalFile,

        OWLManager = Packages.org.semanticweb.owlapi.apibinding.OWLManager,
        IRI = Packages.org.semanticweb.owlapi.model.IRI
        ;

    if (!gufoIris.generate) return;

    ontologyManager = OWLManager.createOWLOntologyManager();
    gufoLocalFile = fileFromPath(options.gufo.localFile);
    gufo = !gufoLocalFile.exists() ?
        ontologyManager.loadOntology(IRI.create(options.gufo.iri)) :
        ontologyManager.loadOntologyFromOntologyDocument(gufoLocalFile);
    gufo.classesInSignature
            .stream()
            .map(owlClass => owlClass.IRI)
            .forEach(iri => gufoIris.classIRIs.push(iri))
    ;
},

/**
 * This step generates a plain text file listing the class names of implemented
 * rules. The generated file is used in runtime by a RuleLoader instance to find
 * the rule classes.
 */
function generateRuleListResource() {
    var
        javaSources = "${basedir}/src/main/java",
        rulesRelativeDir = "${validation.rules.package}".replace(/\./g, "/"),
        rulesDirectory = fileFromPath(javaSources + "/" + rulesRelativeDir),
        writer = fileWriter(
            [ options.generated.resources ].concat(
                options.validation.package.split("."),
                options.validation.ruleList
            ).join(File.separator)
        ),
        visited = {}
        ;

    function listFiles(parent, prefix) {
        if (visited[parent.canonicalPath]) {
            return;
        }
        visited[parent.canonicalPath] = true;
        parent.listFiles().forEach(file => {
            var
                fileName = String(file.name)
                ;
            if (file.isDirectory()) {
                listFiles(file, prefix + "." + fileName);
            } else if (fileName.endsWith(".java")) {
                writer.append(prefix)
                        .append(".")
                        .append(fileName.replace(/.java$/, ""))
                        .append("\n");
            }
        });
    }

    listFiles(rulesDirectory, "${validation.rules.package}");
    writer.close();
},

/**
 * This step generates a plain text file listing the class names of implemented
 * rules. The generated file is used in runtime by a RuleLoader instance to find
 * the rule classes.
 */
function generatePublicClassesSetAndTreeHierarchy() {
    var
        configData,

        tree = gufoIris.tree,
        treeClassNames = gufoIris.treeClassNames,
        parent,
        prefixes = {},
        pattern, match,
        indentations = [ { index: 0, indentation: -1 } ],
        publicClassNames = gufoIris.publicClassNames,
        states, state, stateIndex = -1
        ;
    if (!gufoIris.generate) return;

    configData = fileLines(gufoIris.ufoTreeFile),

    tree.push({
        childrenCount: 0
    });
    states = [
        {   // First state: Prefixes
            enter: ()=> pattern = /\s*@prefix\s+(.*?):\s*<(.*?)>\s*\.\s*$/,
            onMatch: match => {
                prefixes[match[1]] = match[2];
                gufoIris.prefixes.push({
                    prefix: match[1],
                    namespace: match[2]
                });
                gufoIris.nsToPrefix[match[2]] = match[1].toUpperCase();
            }
        },
        {   // Second state: Hierarchy and public classes
            enter: ()=> pattern = /(\s*)(-|\+)\s+(.*?):(.*)\s*/,
            onMatch: match => {
                var
                    indentation = match[1].length,
                    publicity = match[2] === "+",
                    prefix = match[3],
                    name = match[4],

                    levelInfo
                    ;
                while (indentations.length) {
                    levelInfo = indentations.pop();
                    if (indentation > levelInfo.indentation) {
                        indentations.push(levelInfo);
                        indentations.push({
                            index: tree.length,
                            indentation: indentation
                        });
                        break;
                    }
                }
                parent = tree[levelInfo.index];
                tree.push(treeClassNames[name] = {
                    namespace: prefixes[prefix],
                    shortForm: name,
                    childrenCount: 0,
                    parentShortForm: parent.shortForm,
                    index: parent.childrenCount++
                });
                if (publicity) {
                    publicClassNames.push(name);
                }
            }
        },
    ]
    configData.forEach(line => {
        if (line.startsWith("--")) {
            state = states[++stateIndex];
            state.enter();
        } else if (pattern) {
            match = pattern.exec(line);
            if (match) {
                state.onMatch(match);
            }
        }
    });
    tree.shift();
},

function generateGufoIrisClassFile() {
    var
        output,
        replace,

        passThrough,
        optionsRegExp = /options[._]([a-zA-Z._]+)/g,
        changeState = /\s*\/\/\s*foreach\s+([a-zA-Z]+)\s*(\{)?\s*/,
        endLoop = /\s*\/\/\s*\}\s*/,
        loopObject, storedLines,
        foreach
        ;

    if (!gufoIris.generate) return;

    foreach = {
        "void": {
            items: [],
            map: {}
        },
        "namespace": {
            items: gufoIris.prefixes,
            map: {
                PREFIX: prefix => prefix.prefix.toUpperCase(),
                NAMESPACE: prefix => prefix.namespace
            }
        },
        "class": {
            items: gufoIris.classIRIs
                    .filter(iri => gufoIris.nsToPrefix[iri.namespace]),
            map: {
                PREFIX: iri => gufoIris.nsToPrefix[iri.namespace],
                ClassName: iri => iri.shortForm
            }
        },
        "publicClassName": {
            items: gufoIris.publicClassNames,
            map: { ClassName: className => className }
        },
        "treeNode": {
            items: gufoIris.tree,
            map: {
                ParentClassName: node => node.parentShortForm || null,
                ClassName: node => node.shortForm,
                INDEX: node => node.index
            }
        },
        "notInTreeClassName": {
            items: gufoIris.classIRIs
                    .filter(iri => gufoIris.nsToPrefix[iri.namespace])
                    .filter(iri => !gufoIris.treeClassNames[iri.shortForm]),
            map: {
                ClassName: iri => iri.shortForm
            }
        }
    };

    function append(line) {
        output.append(line).append("\n");
    }
    function optionsReplace(fullStr, group) {
        return group.split(/[._]/).reduce((prev, step)=> prev[step], options);
    }
    function passThrough(line) {
        var
            match = changeState.exec(line)
            ;
        loopObject = match && foreach[match[1]]
        if (loopObject) {
            storedLines = [];
            replace = match[2] ? multiLine : oneLine;
        } else {
            append(line.replace(optionsRegExp, optionsReplace));
        }
    }
    function printLines() {
        var
            regExp = new RegExp(Object.keys(loopObject.map).join("|"), "g"),
            template = storedLines.join("\n")
            ;
        loopObject.items.forEach(item => {
            append(template.replace(regExp, (key) =>
                loopObject.map[key](item)))
        });
        replace = passThrough;
    }
    function oneLine(line) {
        storedLines.push(line);
        printLines();
    }
    function multiLine(line) {
        if (endLoop.test(line)) {
            printLines();
        } else {
            storedLines.push(line);
        }
    }
    output = fileWriter(gufoIris.targetFile);
    replace = passThrough;
    fileLines(options.gufoIris.templateSource)
        .forEach(line => replace(line))
        ;
    output.close();
},

function changeReleaseScriptFilePermissions() {
    fileFromPath("${scripts.target.directory}/release.sh").setExecutable(true);
},

function finished() { }

]);
