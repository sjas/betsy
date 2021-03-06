package configuration

import betsy.Betsy
import betsy.data.Engine
import betsy.data.Process
import configuration.processes.Processes
import betsy.executables.CompositeSequential

class TestUsingParameters {

    public static void main(String[] args) {
        CliBuilder cli = new CliBuilder(usage: "[options] <engines> <process>")
        cli.s("skip reinstalling each engine for each process")

        def options = cli.parse(args)
        if(options == null || options == false){
            println cli.usage()
            return
        }

        if(options.s) {
            println "Reinstalling engine for each process"
        }

        List<Engine> engines = parseEngines(options.arguments() as String[]).unique()
        List<Process> processes = parseProcesses(options.arguments() as String[]).unique()

        println "Engines: ${engines.collect{it.name}}"
        println "Processes: ${processes.size() < 10 ? processes.collect{it.bpelFileNameWithoutExtension} : processes.size()}"

        if(options.s) {
            new Betsy(engines: engines, processes: processes).execute()
        } else {
            new Betsy(engines: engines, processes: processes, composite: new CompositeSequential()).execute()
        }

    }

    private static List<Engine> parseEngines(String[] args) {
        if (args.length == 0 || "ALL" == args[0]) {
            Engine.availableEngines()
        } else {
            Engine.build(args[0].toLowerCase().split(",") as List<String>)
        }
    }

    private static List<Process> parseProcesses(String[] args) {
        if( args.length == 0 ){
            ["ALL"].collect() { new Processes().get(it) }.flatten()
        } else {
            args[1].split(",").collect() { new Processes().get(it) }.flatten()
        }
    }

}
