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
    private ArrayList<Double> probInfected;
    private ArrayList<Double> probVaccine;
    private ArrayList<Double> contagion;
    private ArrayList<Double> infectiousness;
    private ArrayList<Integer> sexOnInfection;
    private ArrayList<Integer> sexOnVaccine;
    private ArrayList<Integer> vaccineOnInfection;
    private ArrayList<Double> promiscuityPopulation;

    // CONSTRUCTOR OF FILEPARAMS
    public FileParams(){
        this.probInfected = new ArrayList<>();
        this.probVaccine = new ArrayList<>();
        this.contagion = new ArrayList<>();
        this.infectiousness = new ArrayList<>();
        this.sexOnInfection  = new ArrayList<>();
        this.sexOnVaccine  = new ArrayList<>();
        this.vaccineOnInfection = new ArrayList<>();
        this.promiscuityPopulation  = new ArrayList<>();
    }

    // DEFINE GETTERS AND SETTERS FOR THE PROPERTIES OF FILEPARAMS
    public double getProbInfected(int index){
        return this.probInfected.get(index);
    }
    public void addProbInfected(Double value){
        this.probInfected.add(value);
    }
    public double getProbVaccine(int index){
        return this.probInfected.get(index);
    }
    public void addProbVaccine(Double value){
        this.probVaccine.add(value);
    }
    public double getContagion(int index){
        return this.contagion.get(index);
    }
    public void addContagion(Double value){
        this.contagion.add(value);
    }
    public double getInfectiousness(int index){return this.infectiousness.get(index);}
    public void addInfectiousness(Double value){this.infectiousness.add(value);}
    public int getSexOnInfection(int index){
        return this.sexOnInfection.get(index);
    }
    public void addSexOnInfection(Integer value){
        this.sexOnInfection.add(value);
    }
    public int getSexOnVaccine(int index){
        return this.sexOnVaccine.get(index);
    }
    public void addSexOnVaccine(int value){
        this.sexOnVaccine.add(value);
    }
    public int getVaccineOnInfection(int index){
        return this.sexOnVaccine.get(index);
    }
    public void addVaccineOnInfection(int value){
        this.vaccineOnInfection.add(value);
    }
    public double getPromiscuityPopulation(int index){
        return this.promiscuityPopulation.get(index);
    }
    public void addPromiscuityPopulation(double value){
        this.promiscuityPopulation.add(value);
    }
}
