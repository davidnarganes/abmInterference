/* Created by David Narganes on 01/05/2018.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.jfree.data.xy.XYSeries;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.network.NetworkPortrayal2D;
import sim.portrayal.network.SimpleEdgePortrayal2D;
import sim.portrayal.network.SpatialNetwork2D;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.portrayal.simple.MovablePortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.Bag;
import sim.util.media.chart.TimeSeriesChartGenerator;
import javax.swing.*;
import java.awt.*;

/** GUI CONTROL
 * To control the graphic display of the agents over the simulation
 * Careful not to write any important part of the simulation here: just display
 *
 */
public class GUI extends GUIState {

    private Display2D display;
    private JFrame displayFrame;
    private ContinuousPortrayal2D yardPortrayal = new ContinuousPortrayal2D();
    private NetworkPortrayal2D peersPortrayal = new NetworkPortrayal2D();
    private TimeSeriesChartGenerator cumDistanceChart;
    private JFrame cumDistanceChartFrame;
    private TimeSeriesChartGenerator numInfectedVaccinatedChart;
    private JFrame numInfectedVaccinatedChartFrame;

    /** GUI CONSTRUCTORS
     * There will be two:
     * 1. Default and empty, providing the current wall-clock time
     * 2. Takes the SimState, getting the first constructor
     */

    private GUI(){
        super(new City (System.currentTimeMillis()));
    }
    private GUI(SimState state){
        super(state);
    }

    /** GUI START
     * Initialise the GUI
     * Setup the display
     * Called when pressing `Start` in the GUI
     */

    public Object getSimulationInspectedObject(){
        return state;
    }

    @Override
    public Inspector getInspector(){
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
    }

    @Override
    public void start(){
        super.start();
        setupPortrayals();

        XYSeries mean_series, max_series, min_series, numInfectedSeries, numVaccinatedSeries;

        // CUMULATIVE DISTANCE CHART
        cumDistanceChart.removeAllSeries();
        mean_series = new XYSeries("Mean cumulative distance",false);
        max_series = new XYSeries("Max cumulative distance",false);
        min_series = new XYSeries("Min cumulative distance",false);
        cumDistanceChart.addSeries(mean_series,null);
        cumDistanceChart.addSeries(max_series,null);
        cumDistanceChart.addSeries(min_series,null);
        scheduleRepeatingImmediatelyAfter(new Steppable() {

            @Override
            public void step(SimState state) {
                City city = (City) state;
                double x = city.schedule.getSteps();

                Bag agents = city.yard.getAllObjects();
                double cumDistance;
                double minCumDistance = Double.POSITIVE_INFINITY;
                double maxCumDistance = 0;
                double cumDistanceSum = 0;
                long numPatients = agents.size();
                for(int i = 0; i < numPatients; i++){
                    cumDistance = ((Patient) agents.get(i)).getIndirectInferference();
                    cumDistanceSum += cumDistance;
                    if (cumDistance > maxCumDistance){
                        maxCumDistance = cumDistance;
                    }
                    if(cumDistance < minCumDistance){
                        minCumDistance = cumDistance;
                    }
                }

                // ADD THE DATA
                if (x >= state.schedule.EPOCH && x < state.schedule.AFTER_SIMULATION){
                    max_series.add(x,maxCumDistance,false);
                    min_series.add(x,minCumDistance,false);
                    mean_series.add(x, cumDistanceSum/numPatients,false);

                    cumDistanceChart.updateChartWithin(state.schedule.getSteps(), 1000);
                }
            }
        });

        // NUMBER INFECTED VACCINATED CHART
        numInfectedVaccinatedChart.removeAllSeries();
        numInfectedSeries = new XYSeries("Number Infected", false);
        numVaccinatedSeries = new XYSeries("Number Vaccinated",false);
        numInfectedVaccinatedChart.addSeries(numInfectedSeries,null);
        numInfectedVaccinatedChart.addSeries(numVaccinatedSeries,null);
        scheduleRepeatingImmediatelyAfter(new Steppable() {

            public void step(SimState state) {
                City city = (City) state;
                Patient patient = new Patient("name",city);

                double x = city.schedule.getSteps();
                double infected = patient.countInfected(city);
                double vaccinated = patient.countVaccinated(city);

                // ADD THE DATA
                if (x >= state.schedule.EPOCH && x < state.schedule.AFTER_SIMULATION){
                    numInfectedSeries.add(x,infected,false); // don't redraw data immediately
                    numVaccinatedSeries.add(x,vaccinated,false);
                    numInfectedVaccinatedChart.updateChartWithin(state.schedule.getSteps(),1000);
                }
            }
        });

    }

    /** SETUP PORTRAYALS
     * Movable
     * Circle when selected
     * Label based on agent attributes
     * Depending on properties each agent will be an Oval with different colors:
     * RED: Infected
     * GREEN: Vaccinated
     * YELLOW: Infected^Vaccinated
     */
    private void setupPortrayals() {
        City city = (City) state;

        // HOW TO PORTRAY
        yardPortrayal.setField(city.yard);
        yardPortrayal.setPortrayalForAll(
                new MovablePortrayal2D(
                        new CircledPortrayal2D(
                                new LabelledPortrayal2D(
                                        new OvalPortrayal2D(){
                                            public void draw(Object object,
                                                             Graphics2D graphics,
                                                             DrawInfo2D info){
                                                Patient patient = (Patient) object;
                                                int infectedColor = 0;
                                                int treatedColor = 0;

                                                // SET DIFFERENT COLORS DEPENDING ON PROPERTIES
                                                if (patient.getInfected()){
                                                    infectedColor = 255;
                                                }
                                                if (patient.getVaccine()){
                                                    treatedColor = 255;
                                                }

                                                paint = new Color(infectedColor,0,treatedColor);
                                                super.draw(object,graphics,info);
                                            }
                                        },
                                        5.0, null, Color.blue, true),
                                0, 5.0, Color.green, true
                        )
                )
        );

        // NETWORK PORTRAYAL
        peersPortrayal.setField(new SpatialNetwork2D(city.yard, city.peers));
        peersPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D(Color.lightGray, null));

        // RESCHEDULE THE DISPLAY
        display.reset();

        // REDRAW PORTRAYAL
        display.repaint();
    }

    /** INIT GUI
     * Called when GUI is created
     * It creates a new display of x,y pixels
     * False clipping for allowing the agents to cross the GUI boundaries
     * @param c is the visualisation controller
     */
    public void init(Controller c){

        // DEFINE DIMENSIONS OF NEW CHARTS
        Dimension dim = new Dimension();
        dim.setSize(550,380);

        super.init(c);
        display = new Display2D(700,500,this);
        display.setClipping(false);
        displayFrame = display.createFrame();
        displayFrame.setTitle("Smart City Display");
        c.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        display.attach(peersPortrayal, "Friends");
        display.attach(yardPortrayal, "City");

        // CHART CUMULATIVE DISTANCE
        cumDistanceChart = new TimeSeriesChartGenerator();
        cumDistanceChart.setTitle("Infected Cumulative Distance");
        cumDistanceChart.setXAxisLabel("Step");
        cumDistanceChart.setYAxisLabel("Infected cumulative distance");
        cumDistanceChart.setPreferredSize(dim);
        cumDistanceChartFrame = new JFrame();
        cumDistanceChartFrame.setTitle("Cumulative Distance Display");
        cumDistanceChartFrame.setLocation(800,0);
        cumDistanceChartFrame.add(cumDistanceChart);

        // MOVE THE CHART A PIACERE
        cumDistanceChartFrame.setVisible(true);
        cumDistanceChartFrame.pack();
        controller.registerFrame(cumDistanceChartFrame);

        // CHART INFECTED^VACCINATED
        numInfectedVaccinatedChart = new TimeSeriesChartGenerator();
        numInfectedVaccinatedChart.setTitle("Number of vaccinated and infected patients");
        numInfectedVaccinatedChart.setXAxisLabel("Step");
        numInfectedVaccinatedChart.setYAxisLabel("Counts");
        numInfectedVaccinatedChart.setPreferredSize(dim);
        numInfectedVaccinatedChartFrame = new JFrame();
        numInfectedVaccinatedChartFrame.setTitle("Counts of properties display");
        numInfectedVaccinatedChartFrame.setLocation(800,450);
        numInfectedVaccinatedChartFrame.add(numInfectedVaccinatedChart);
        numInfectedVaccinatedChartFrame.setVisible(true);
        numInfectedVaccinatedChartFrame.pack();
        controller.registerFrame(numInfectedVaccinatedChartFrame);
    }

    @Override
    public void finish(){
        super.finish();

        cumDistanceChart.update(state.schedule.getSteps(),true);
        cumDistanceChart.repaint();
//        cumDistanceChart.startMovie();

        numInfectedVaccinatedChart.update(state.schedule.getSteps(),true);
        numInfectedVaccinatedChart.repaint();
//        cumDistanceChart.startMovie();
    }

    /** QUIT GUI
     * To initialise when the GUI will be destroyed, to clear it up
     * Set the display to null if it is not null already (not need to do it twice)
     * Display to null to assist the garbage collection
     */

    @Override
    public void quit(){
        super.quit();
        if (displayFrame != null) displayFrame.dispose();
        displayFrame = null;
        display = null;

        // CUMULATIVE DISTANCE CHART
        cumDistanceChart.update(state.schedule.getSteps(),true);
        cumDistanceChart.repaint();
//        cumDistanceChart.stopMovie();
        if(cumDistanceChartFrame != null){
            cumDistanceChartFrame.dispose();
        }
        cumDistanceChart = null;

        // NUMBER VACCINATED INFECTED CHART
        numInfectedVaccinatedChart.update(state.schedule.getSteps(),true);
        numInfectedVaccinatedChart.repaint();
//        numInfectedVaccinatedChart.stopMovie();
        if(numInfectedVaccinatedChartFrame != null){
            numInfectedVaccinatedChartFrame.dispose();
        }
        numInfectedVaccinatedChart = null;
    }

    public static void main (String[] args){
        GUI vid = new GUI();
        Console c = new Console (vid);
        c.setVisible(true);
    }
}
