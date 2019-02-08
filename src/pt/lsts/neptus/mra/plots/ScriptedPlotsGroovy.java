/*
 * Copyright (c) 2004-2019 Universidade do Porto - Faculdade de Engenharia
 * Laboratório de Sistemas e Tecnologia Subaquática (LSTS)
 * All rights reserved.
 * Rua Dr. Roberto Frias s/n, sala I203, 4200-465 Porto, Portugal
 *
 * This file is part of Neptus, Command and Control Framework.
 *
 * Commercial Licence Usage
 * Licencees holding valid commercial Neptus licences may use this file
 * in accordance with the commercial licence agreement provided with the
 * Software or, alternatively, in accordance with the terms contained in a
 * written agreement between you and Universidade do Porto. For licensing
 * terms, conditions, and further information contact lsts@fe.up.pt.
 *
 * Modified European Union Public Licence - EUPL v.1.1 Usage
 * Alternatively, this file may be used under the terms of the Modified EUPL,
 * Version 1.1 only (the "Licence"), appearing in the file LICENSE.md
 * included in the packaging of this file. You may not use this work
 * except in compliance with the Licence. Unless required by applicable
 * law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific
 * language governing permissions and limitations at
 * https://github.com/LSTS/neptus/blob/develop/LICENSE.md
 * and http://ec.europa.eu/idabc/eupl.html.
 *
 * For more information please see <http://lsts.fe.up.pt/neptus>.
 *
 * Author: keila
 * Feb 8, 2019
 */
package pt.lsts.neptus.mra.plots;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashMap;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import groovy.lang.GroovyShell;
import pt.lsts.imc.lsf.LsfIndex;
import pt.lsts.neptus.mra.LogMarker;
import pt.lsts.neptus.mra.MRAPanel;
import pt.lsts.neptus.util.GuiUtils;

/**
 * @author keila
 *
 */
public class ScriptedPlotsGroovy extends MRATimeSeriesPlot {
    
    private TimeSeriesCollection timeseries = new TimeSeriesCollection();
    private GroovyShell shell;
    private LsfIndex index;
    private final String scriptPath;


    /**
     * @param panel
     */
    public ScriptedPlotsGroovy(MRAPanel panel,String path) {
        super(panel);
        scriptPath = path;
        // init shell
        CompilerConfiguration cnfg = new CompilerConfiguration();
        ImportCustomizer imports = new ImportCustomizer();
        imports.addStarImports("pt.lsts.imc", "java.lang.Math","pt.lsts.neptus.mra.plots");
        imports.addStaticStars("pt.lsts.neptus.mra.plots.ScriptedPlotGroovy","pt.lsts.neptus.plugins.plots.groovy.GroovyPlot");
        cnfg.addCompilationCustomizers(imports);
        shell = new GroovyShell(this.getClass().getClassLoader(), cnfg);
    }

    /* (non-Javadoc)
     * @see pt.lsts.neptus.mra.plots.MRATimeSeriesPlot#canBeApplied(pt.lsts.imc.lsf.LsfIndex)
     */
    @Override
    public boolean canBeApplied(LsfIndex index) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see pt.lsts.neptus.mra.plots.MRATimeSeriesPlot#process(pt.lsts.imc.lsf.LsfIndex)
     */
    @Override
    public void process(LsfIndex source) {
        // TODO call script
        runScript(scriptPath);

    }
    
    /**
     * Runs the Groovy script after verifying its validity by parsing it.
     * 
     * @param script Text script
     */
    public void runScript(String path) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
          shell.setVariable("plot", this);
          String defplot = "configPlot plot";
          shell.evaluate(defplot);
          String script = sb.toString();  
          shell.parse(script);
          shell.evaluate(script);
          reader.close();
        }
        catch (Exception e) {
            GuiUtils.errorMessage(super.mraPanel, "Error Parsing Script", e.getLocalizedMessage());
        }
    }
    
    /**
     * Adds a new time series to the existing plot. If the series already exists, it updates it.
     * 
     * @param allTsc the TimeSeries to be added
     */
    public void addTimeSerie(String id, TimeSeries ts) {
        if (timeseries.getSeries(id) == null) {
            //ts.setMaximumItemCount(super.timestep);
            timeseries.addSeries(ts);
        }
        else
            timeseries.getSeries(id).addOrUpdate(ts.getDataItem(0));
    }
    public void mark (double time,String label  ) {
        mraPanel.addMarker(new LogMarker(label, time * 1000,0,0));
    }
    
    public LinkedHashMap getDataFromExpr (String expr) {
        //TODO
        String msg, entity, field;
        String[] fields = expr.split(".");
        if(fields.length == 2) {
            msg = fields[0];
            field = fields[1];
            entity = null;
        }
        else if(fields.length == 3)
        {
            msg = fields[0];
            entity = fields[1];
            field = fields[2];
        }
        LinkedHashMap<String,Double> result = new LinkedHashMap<>();
        
        //TODO retrieve data
        
        return result;
    }
}
