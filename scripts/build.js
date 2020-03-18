var
    options = {
        gufo: {
            iri: "${gufo.iri}",
            localFile: "${gufo.ontology.file}"
        },
        generated: {
            sources: "${js.generated.sources}",
            resources: "${js.generated.resources}"
        },
        validation: {
            GufoIrisClassName: "GufoIris",
            package: "${validation.main.package}",
            rulesPackage: "${validation.rules.package}",
            ruleList: "${validation.rules.resource.name}",
        }
    },

    // Making Netbeans happy
    java, Packages, arguments
    ;

(function generateGufoIriClass() {
    var
        targetFile,
        classList = [],
        ontologyManager,
        gufo, gufoLocalFile,
        outputStream,
        outputWriter,

        OWLManager = Packages.org.semanticweb.owlapi.apibinding.OWLManager,
        IRI = Packages.org.semanticweb.owlapi.model.IRI,
        File = java.io.File
        ;
    targetFile = new java.io.File(
        [ options.generated.sources ].concat(
            options.validation.package.split("."),
            [ options.validation.GufoIrisClassName ]
        ).join("${file.separator}") + ".java"
    );

    if (!targetFile.exists()) {
        ontologyManager = OWLManager.createOWLOntologyManager();
        gufoLocalFile = new File(options.gufo.localFile);
        gufo = !gufoLocalFile.exists() ?
            ontologyManager.loadOntology(IRI.create(options.gufo.iri)) :
            ontologyManager.loadOntologyFromOntologyDocument(gufoLocalFile);
        gufo.classesInSignature
                .stream()
                .map(owlClass => owlClass.IRI)
                .filter(iri => iri.namespace == options.gufo.iri)
                .forEach(iri => classList.push(iri.shortForm))
        ;

        targetFile.parentFile.mkdirs();
        outputStream = new java.io.FileOutputStream(targetFile);
        outputWriter = new java.io.OutputStreamWriter(outputStream);
        outputWriter.append(
            [
                "package options.validation.package;",
                "",
                "import org.semanticweb.owlapi.model.IRI;",
                "",
                "public class options.validation.GufoIrisClassName {",
                "    ",
                "    public static final String GUFO = \"options.gufo.iri\";",
                classList
                    .map(
                        className => [
                            "    ",
                            "public static final IRI ClassName = ",
                            "IRI.create(GUFO, \"ClassName\");"
                        ].join("").replace(/ClassName/g, className)
                    ).join("\n"),
                "}"
            ].join("\n")
            .replace(/options\.([a-zA-Z.]+)/g, (fullStr, group) =>
                group.split(".").reduce((prev, step)=> prev[step], options))
        );
        outputWriter.close();
        outputStream.close();
    }
}) ();

(function changeReleaseScriptFilePermissions() {
    var
        File = java.io.File,
        directory = new File("${scripts.target.directory}"),
        file = new File(directory, "release.sh")
        ;
    file.setExecutable(true);
}) ();
