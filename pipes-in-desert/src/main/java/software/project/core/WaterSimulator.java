package software.project.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import software.project.map.Cistern;
import software.project.map.Element;
import software.project.map.GameMap;
import software.project.map.Pipe;
import software.project.map.PipeEnd;
import software.project.map.Pump;
import software.project.map.Spring;

public class WaterSimulator {

    private final GameMap map;

    public static class Flow {
        List<Element> elements;
        int currentElement;

        Flow(List<Element> elements, int currentElement) {
            this.elements = elements;
            this.currentElement = currentElement;
        }
    }

    List<Flow> flows;

    // DO ALL ACTIONS, THEN Simulate Water flow cannot be in middle
    /*
     * 1. players do actions
     * 2. timer expires OR turn ends
     * 3. simulate water tick
     * 4. check win conditions
     * 5. repeat
     * 
     * Basically simulation runs every couple of seconds, and player input is just
     * applied into the next tick
     * 
     */

    public WaterSimulator(GameMap map) {
        this.map = map;
        this.flows = new ArrayList<>();
    }

    public int tickFlow() {
        List<Flow> newFlows = generateNewFlow();
        int lostWater = 0;
        flows.addAll(newFlows);
        List<Flow> markedForDeletion = new ArrayList<>();
        List<Flow> markedForAddition = new ArrayList<>();
        // move all flows by 1
        for (Flow flow : flows) {
            if (flow.currentElement < 0)
                continue;
            // transfer water here
            // from element to element
            // if we are done just add it to the marked for deletion
            // if element is a pump check if it changed and rebuild its path
            int currentElement = flow.currentElement;
            List<Element> elements = flow.elements;
            // BUG WE DONT DEAL WITH SINGLE ELEMENTS FLOWS!
            // if(currentElement >= elements.size() - 1) {
            // // mark for deletion
            // markedForDeletion.add(flow);
            // continue;
            // }
            // deal with finishing or single element flows
            if (currentElement >= elements.size() - 1) {
                // last element
                if (currentElement == elements.size() - 1) {
                    // what happens if we add an element?
                    Element last = flow.elements.get(currentElement);
                    List<Element> potentialNewPath = buildPath(last);
                    // there is something added!!!!!
                    if (potentialNewPath.size() > 1) {
                        markedForDeletion.add(flow);
                        elements.clear();
                        List<Element> newEl = new ArrayList<>(potentialNewPath);
                        markedForAddition.add(
                                new Flow(newEl, 0));
                        potentialNewPath.subList(1, potentialNewPath.size());
                        continue;
                    }

                    int waterToLose = last.moveWater();
                    lostWater += waterToLose;
                }
                markedForDeletion.add(flow);
                continue;
            }
            // basically move water now
            // a tick meaning changed, one tick is water flowing through the pipe
            // if pipe is broken then we lose this water

            // if pump is current element we rebuild the graph in case of changes
            if (elements.get(currentElement) instanceof Pump p) {

                if (elements.get(currentElement + 1) != p.getOutgoingPipe()) {
                    // CHANGED
                    Pipe pipe = p.getOutgoingPipe();
                    // basically the pipe was removed
                    if (pipe == null) {
                        elements.subList(currentElement + 1, elements.size());
                        continue;
                    }
                    var directionEnd = pipe.resolveEnd(p.getOutputPipe());
                    if (directionEnd == null)
                        continue;
                    List<Element> newFlow = new ArrayList<>(elements.subList(currentElement + 1, elements.size()));
                    markedForAddition.add(new Flow(newFlow, 0));
                    markedForDeletion.add(flow);
                    elements.subList(currentElement + 1, elements.size()).clear();
                    elements.addAll(buildPath(pipe));
                }
            }
            if (elements.get(currentElement) instanceof Pipe p) {
                var end = p.resolveOutputEnd();
                if(end == null) continue;
                if(end.getEnd().connectedTo != elements.get(currentElement + 1)) {
                  // change happened in the pipe
                    List<Element> newPath = buildPath(p);
                    markedForAddition.add(new Flow(newPath,0));
                    markedForDeletion.add(flow);
                    elements.subList(currentElement + 1, elements.size()).clear();
                    continue;
                }
            }
            int moveFrom = elements.get(currentElement).moveWater();
            elements.get(currentElement + 1).receiveWater(moveFrom);

        }
        Set<Element> toCommit = new HashSet<>();

        for (Flow flow : flows) {
            if (flow.currentElement < 0) {
                flow.currentElement++;
                continue;
            }
            if (flow.currentElement < flow.elements.size()) {
                toCommit.add(flow.elements.get(flow.currentElement));
                if (flow.currentElement + 1 < flow.elements.size())
                    toCommit.add(flow.elements.get(flow.currentElement + 1));
            }
            flow.currentElement++;
        }

        for (Element e : toCommit) {
            lostWater += e.commit();
        }
        flows.removeAll(markedForDeletion);
        flows.addAll(markedForAddition);
        return lostWater;
    }

    public List<Flow> generateNewFlow() {
        List<Flow> flows = new ArrayList<>();

        for (Spring spring : map.getAllSprings()) {
            for (PipeEnd pipeEnd : spring.getConnections()) {

                if (pipeEnd.isFree())
                    continue;

                Pipe pipe = pipeEnd.pipe;
                List<Element> path = new ArrayList<>();
                path.add(spring);
                Pipe.DirectionEnd end = pipe.resolveEnd(pipeEnd);
                if (end == null) {
                    continue;
                }

                end.setInput(true);
                path.addAll(buildPath(pipe)); // bfs includes the starting pipe

                flows.add(new Flow(path, -1));
            }
        }

        return flows;
    }

    private List<Element> buildPath(Element startingElement) {
        Element current = startingElement;
        Set<Element> visited = new HashSet<>();
        List<Element> path = new ArrayList<>();
        if (startingElement instanceof Pipe p) {

            Pipe.DirectionEnd end = p.resolveInputEnd();
            if (end == null)
                return new ArrayList<>();
        }

        while (current != null) {
            if (visited.contains(current)) {
                path.add(current); // if we looped
                break;
            }
            visited.add(current);
            // add current element
            path.add(current);
            if (current instanceof Pipe pipe) {
                // check for work here
                // set the direction
                Pipe.DirectionEnd input = pipe.resolveInputEnd();
                Pipe.DirectionEnd output = pipe.resolveOutputEnd();
                if (pipe.isMeetingAtSamePipe()) {
                    pipe.setConflict(true);
                    break;
                    // we dont need it but for clarity
                }
                // if(pipe.isBroken()) {
                // break;
                // }
                if (input == null || output == null)
                    break;
                // check for conflict
                if (output.getEnd().connectedTo instanceof Pump p && p.getOutgoingPipe() == pipe) {
                    // LOOP CONFLICT
                    // STOP AT THIS ELEMENT
                    pipe.setConflict(true);
                    break;
                }

                Element next = output.getEnd().connectedTo;
                if (next instanceof Pipe nextPipe) {
                    Pipe.DirectionEnd nextEnd = null;
                    if (nextPipe.getEnd1().connectedTo == pipe) {
                        nextEnd = nextPipe.resolveEnd(nextPipe.getEnd1());
                    } else if (nextPipe.getEnd2().connectedTo == pipe) {
                        nextEnd = nextPipe.resolveEnd(nextPipe.getEnd2());
                    }
                    if (nextEnd != null) {
                        nextEnd.setInput(true);
                    }
                }
                if (output.getEnd().connectedTo instanceof Pump p) {
                    if (p.getInputPipe().pipe != pipe) {
                        // pipe is not connected to this pump
                        break;
                    }
                }
                current = output.getEnd().connectedTo;

            } else if (current instanceof Pump pump) {
                // check here but we got everything
                // if(pump.isBroken()){
                // break;
                // }
                PipeEnd end = pump.getOutputPipe();
                if (end == null)
                    break;
                Pipe pipe = pump.getOutgoingPipe();
                if (pipe == null)
                    break;
                pipe.resolveEnd(end).setInput(true);
                current = pipe;

            } else if (current instanceof Cistern) {
                current = null;
            } else if (current instanceof Spring) {
                current = null;
            } else {
                current = null;
            }
        }
        return path;
    }
    // do traversal and get directions we set directions of pipes here (if there is
    // a conflict we set it here)
    // start from all the sources
    // private List<Element> buildPath(Pipe startingPipe, Pipe.DirectionEnd end) {
    //
    // // we check the pipes conflicts
    // // check the pump conflicts as well
    // // we can do polymorphism but for now quickly instanceof in any case we need
    // List<Element> path = new ArrayList<>();
    // Element current = startingPipe;
    // end.setInput(true);
    // Set<Element> visited = new HashSet<>();
    //
    // // make sure to not double add first one
    // while(current != null) {
    // if(visited.contains(current)) {
    // path.add(current); // if we looped
    // break;
    // }
    // visited.add(current);
    // // add current element
    // path.add(current);
    // if(current instanceof Pipe pipe) {
    // // check for work here
    // // set the direction
    // Pipe.DirectionEnd input = pipe.resolveInputEnd();
    // Pipe.DirectionEnd output = pipe.resolveOutputEnd();
    // if(pipe.isMeetingAtSamePipe()) {
    // pipe.setConflict(true);
    // break;
    // // we dont need it but for clarity
    // }
    //// if(pipe.isBroken()) {
    //// break;
    //// }
    // if(input == null || output == null) break;
    // // check for conflict
    // if(output.getEnd().connectedTo instanceof Pump p && p.getOutgoingPipe() ==
    // pipe) {
    // // LOOP CONFLICT
    // // STOP AT THIS ELEMENT
    // pipe.setConflict(true);
    // break;
    // } else {
    // current = output.getEnd().connectedTo;
    //
    // }
    // }
    // else if(current instanceof Pump pump) {
    // // check here but we got everything
    //// if(pump.isBroken()){
    //// break;
    //// }
    // pump.getOutgoingPipe().resolveEnd(pump.getOutputPipe()).setInput(true);
    // current = pump.getOutgoingPipe();
    //
    // } else if(current instanceof Cistern) {
    // current = null;
    // } else if(current instanceof Spring){
    // current = null;
    // }
    // else {
    // current = null;
    // }
    // }
    //
    // return path;
    //
    // }

    private List<PipeEnd> getAllPipeEnds() {
        List<PipeEnd> ends = new ArrayList<>();

        for (Pipe p : map.getAllPipes()) {
            ends.add(p.getEnd1());
            ends.add(p.getEnd2());
        }

        return ends;
    }
}
