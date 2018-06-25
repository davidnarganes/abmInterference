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

import java.util.ArrayList;

/** FILEPARAMS CLASS
The columns of the `params` file will be stored as properties of a FileParams object
 */

public class FileParams {
    private ArrayList<Integer> numPatients;
    private ArrayList<Double> probInfected;
    private ArrayList<Double> probVaccine;
    private ArrayList<Double> lambda;
    private ArrayList<Double> contagion;
    private ArrayList<Double> infectiousness;
    private ArrayList<Double> sexOnVaccine;
    private ArrayList<Double> sexOnInfection;
    private ArrayList<Double> vaccineOnInfection;
    private ArrayList<Double> promiscuityPopulation;
    private ArrayList<Double> maxPartnerForce;
    private ArrayList<Double> randomForce;
    private ArrayList<Double> partnerForce;

    // Constructor
    public FileParams(){
        this.numPatients = new ArrayList<>();
        this.probInfected = new ArrayList<>();
        this.probVaccine = new ArrayList<>();
        this.lambda = new ArrayList<>();
        this.contagion = new ArrayList<>();
        this.infectiousness = new ArrayList<>();
        this.sexOnInfection  = new ArrayList<>();
        this.sexOnVaccine  = new ArrayList<>();
        this.vaccineOnInfection = new ArrayList<>();
        this.promiscuityPopulation  = new ArrayList<>();
        this.maxPartnerForce = new ArrayList<>();
        this.randomForce = new ArrayList<>();
        this.partnerForce = new ArrayList<>();
    }

    // Getters
    public int getNumPatients(int index){return this.numPatients.get(index);}
    public double getProbInfected(int index){return this.probInfected.get(index);}
    public double getProbVaccine(int index){return this.probVaccine.get(index);}
    public double getLambda(int index){return this.lambda.get(index);}
    public double getContagion(int index){return this.contagion.get(index);}
    public double getInfectiousness(int index){return this.infectiousness.get(index);}
    public double getSexOnInfection(int index){return this.sexOnInfection.get(index);}
    public double getSexOnVaccine(int index){return this.sexOnVaccine.get(index);}
    public double getVaccineOnInfection(int index){return this.vaccineOnInfection.get(index);}
    public double getPromiscuityPopulation(int index){return this.promiscuityPopulation.get(index);}
    public double getMaxPartnerForce(int index){return this.maxPartnerForce.get(index);}
    public double getRandomForce(int index){return this.randomForce.get(index);}
    public double getPartnerForce(int index){return this.partnerForce.get(index);}

    // Adders
    public void addNumPatients(int value){this.numPatients.add(value);}
    public void addProbInfected(double value){this.probInfected.add(value);}
    public void addProbVaccine(double value){this.probVaccine.add(value);}
    public void addLambda(double value){this.lambda.add(value);}
    public void addContagion(double value){this.contagion.add(value);}
    public void addInfectiousness(double value){this.infectiousness.add(value);}
    public void addSexOnInfection(double value){this.sexOnInfection.add(value);}
    public void addSexOnVaccine(double value){this.sexOnVaccine.add(value);}
    public void addVaccineOnInfection(double value){this.vaccineOnInfection.add(value);}
    public void addPromiscuityPopulation(double value){this.promiscuityPopulation.add(value);}
    public void addMaxPartnerForce(double value){this.maxPartnerForce.add(value);}
    public void addRandomForce(double value){this.randomForce.add(value);}
    public void addPartnerForce(double value){this.partnerForce.add(value);}

}
