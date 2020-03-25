((mappings, applyMappings) => applyMappings(mappings)) (

{
    "modelManager": ()=> global.editorKit.OWLModelManager,
    "workspace": ()=> global.editorKit.OWLWorkspace,
    "activeOntology": ()=> modelManager.activeOntology,
    "ontologies": ()=> modelManager.activeOntologies.stream(),
    "signature": ()=> activeOntology.signature.stream(),
    "classes": ()=> signature.filter(c=> c.isOWLClass()),
    "alert": function (message) {
        var
            JOptionPane
            ;
        JOptionPane = Packages.javax.swing.JOptionPane;
        JOptionPane.showMessageDialog(workspace, message);
    },
    "log": function (message) {
        logTextArea.append("\n");
        logTextArea.append(Array.apply(Array, arguments).join(" "));
    }
},

function applyMappings(mappings) {
    Object.keys(mappings).forEach(propertyName => {
        var
            arg = mappings[propertyName],
            descriptor
            ;
        switch (typeof arg) {
            case "function":
                if (arg.length === 0) {
                    descriptor = {
                        "get": arg
                    };
                    break;
                }
            default:
                descriptor = {
                    "value": arg
                }
        }
        Object.defineProperty(global, propertyName, descriptor);
    });
});
