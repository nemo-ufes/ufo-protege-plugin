var
    options = {
        gufo: {
            iri: "${gufo.iri}",
            localFile: "${gufo.ontology.file}"
        },
        gufoIris: {
            package: "${plugin.main.package}",
            className: "GufoIris"
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
        targetFile: null,
        prefixes: [],
        nsToPrefix: {},
        tree: []
    },

    // Making Netbeans happy
    java, Packages, arguments,
    File = java.io.File
    ;

function fileWriter(file) {
    var
        outputStream, outputWriter,
        result
        ;
    if (!(file instanceof File)) {
        file = new File(file);
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
        inputStream, inputReader, lineNumberReader
        ;
    if (!(file instanceof File)) {
        file = new File(file);
    }
    inputStream = new java.io.FileInputStream(file);
    inputReader = new java.io.InputStreamReader(inputStream);
    lineNumberReader = new java.io.LineNumberReader(inputReader);

    return {
        forEach: (consumer) => {
            lineNumberReader.lines().forEach(line => consumer(line));
            lineNumberReader.close();
            inputReader.close();
            inputStream.close();
        }
    };
}

(steps => steps.forEach(step => (print(step.name), step())))([

function checkGufoIriClassFile() {
    gufoIris.targetFile = new File(
        [ options.generated.sources ].concat(
            options.gufoIris.package.split("."),
            [ options.gufoIris.className ]
        ).join("${file.separator}") + ".java"
    );
    gufoIris.generate = true || !gufoIris.targetFile.exists();
},

function generateGufoIriClasses() {
    var
        ontologyManager,
        gufo, gufoLocalFile,

        OWLManager = Packages.org.semanticweb.owlapi.apibinding.OWLManager,
        IRI = Packages.org.semanticweb.owlapi.model.IRI
        ;

    if (gufoIris.generate) {
        ontologyManager = OWLManager.createOWLOntologyManager();
        gufoLocalFile = new File(options.gufo.localFile);
        gufo = !gufoLocalFile.exists() ?
            ontologyManager.loadOntology(IRI.create(options.gufo.iri)) :
            ontologyManager.loadOntologyFromOntologyDocument(gufoLocalFile);
        gufo.classesInSignature
                .stream()
                .map(owlClass => owlClass.IRI)
                .forEach(iri => gufoIris.classIRIs.push(iri))
        ;
    }
},

function generateRuleListResource() {
    var
        javaSources = new File("${basedir}/src/main/java"),
        writer = fileWriter(
            [ options.generated.resources ].concat(
                options.validation.package.split("."),
                options.validation.ruleList
            ).join("${file.separator}")
        ),
        rulesDirectory = new File(javaSources,
            "${validation.rules.package}".replace(/\./g, "${file.separator}")),
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

function generatePublicClassesSetAndTreeHierarchy() {
    var
        directory = new File("${scripts.target.directory}"),
        configData = fileLines(new File(directory, "ufo-tree.txt")),

        tree = gufoIris.tree,
        parent,
        prefixes = {},
        pattern, match,
        indentations = [ { index: 0, indentation: -1 } ],
        publicClassNames = gufoIris.publicClassNames,
        states, state, stateIndex = -1
        ;
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
                tree.push({
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

    if (gufoIris.generate) {
        fileWriter(gufoIris.targetFile)
        .append(
            [
                "package options.gufoIris.package;",
                "",
                "import java.util.Collections;",
                "import java.util.Set;",
                "import java.util.HashSet;",
                "import java.util.Map;",
                "import java.util.HashMap;",
                "import org.semanticweb.owlapi.model.IRI;",
                "",
                "// Class generated by build.js",
                "public class options.gufoIris.className {",
                "    ",
                "    // Namespace prefixes",
                gufoIris.prefixes.map(prefix =>
                "    public static final String PREFIX = \"NAMESPACE\";"
                    .replace(/PREFIX/, prefix.prefix.toUpperCase())
                    .replace(/NAMESPACE/, prefix.namespace)
                ).join("\n"),
                "    // IRI of classes in GUFO Ontology",
                gufoIris.classIRIs
                .filter(iri => gufoIris.nsToPrefix[iri.namespace])
                .map(iri => [
                "    public static final IRI ClassName = ",
                            "IRI.create(PREFIX, \"ClassName\");"
                    ].join("")
                    .replace(/PREFIX/g, gufoIris.nsToPrefix[iri.namespace])
                    .replace(/ClassName/g, iri.shortForm)
                ).join("\n"),
                "    ",
                "    // Set of classes expected to be specialized",
                "    public static final Set<IRI> publicClasses;",
                "    static {",
                "        Set<IRI> set = new HashSet<>();",
                gufoIris.publicClassNames.map(className =>
                "        set.add(ClassName);".replace(/ClassName/g, className)
                ).join("\n"),
                "        publicClasses = Collections.unmodifiableSet(set);",
                "    }",
                "    ",
                "    // Map with hierarchyview information",
                "    public static final Map<IRI, HierarchyNode> tree;",
                "    static {",
                "       Map<IRI, HierarchyNode> map = new HashMap<>();",
                "       HierarchyNode nodeFornull = new HierarchyNode(null, null, 0);",
                "       map.put(null, nodeFornull);",
                gufoIris.tree.map(info=>
                    [
                        "       HierarchyNode nodeForClassName;\n",
                        "       nodeForClassName = new HierarchyNode(",
                                    "ClassName, ParentClassName, index);\n",
                        "       map.put(ClassName, nodeForClassName);\n",
                        "       nodeForParentClassName.getChildren().add(ClassName);"
                    ].join("")
                            .replace(/ParentClassName/g, info.parentShortForm || null)
                            .replace(/ClassName/g, info.shortForm)
                            .replace(/index/g, info.index)
                ).join("\n"),
                "       tree = Collections.unmodifiableMap(map);",
                "    }",
                "}"
            ].join("\n")
            .replace(/options\.([a-zA-Z.]+)/g, (fullStr, group) =>
                group.split(".").reduce((prev, step)=> prev[step], options))
        )
        .close();
    }
},

function changeReleaseScriptFilePermissions() {
    var
        directory = new File("${scripts.target.directory}"),
        file = new File(directory, "release.sh")
        ;
    file.setExecutable(true);
}

]);
