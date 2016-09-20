/*
 * Copyright (c) 2004-2016 Universidade do Porto - Faculdade de Engenharia
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
 * European Union Public Licence - EUPL v.1.1 Usage
 * Alternatively, this file may be used under the terms of the EUPL,
 * Version 1.1 only (the "Licence"), appearing in the file LICENSE.md
 * included in the packaging of this file. You may not use this work
 * except in compliance with the Licence. Unless required by applicable
 * law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific
 * language governing permissions and limitations at
 * http://ec.europa.eu/idabc/eupl.html.
 *
 * For more information please see <http://lsts.fe.up.pt/neptus>.
 *
 * Author: Manuel R.
 * 26/08/2016
 */
package pt.lsts.neptus.console.plugins.planning.overview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import net.miginfocom.swing.MigLayout;
import pt.lsts.neptus.console.plugins.planning.PlanEditor;
import pt.lsts.neptus.mp.Maneuver;
import pt.lsts.neptus.types.mission.plan.PlanType;

public class MissionOverviewPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private PlanType selectedPlan = null;
    private Maneuver selectedManeuver = null;
    PlanTableModel model = null;
    private int prevSelectedRow, row, col = -1;

    public MissionOverviewPanel(PlanEditor pE, PlanType plan) {

        selectedPlan = plan;
        setLayout(new MigLayout("", "[][grow]", "[]"));
        model = new PlanTableModel(selectedPlan);
        table = new JTable(model);

        setupTable(pE);

        add(new JScrollPane(table), "cell 0 1 2 1,grow");
        setPreferredSize(getPreferredSize());
    }

    private void setupTable(PlanEditor pE) {
        //Set renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = -4859420619704314087L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                    int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                Color color = model.getRowColour(row, isSelected);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(color);

                return this;
            }
        });


        //Add mouselistener
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JTable table = (JTable) e.getSource();
                row = table.getSelectedRow();

                if (e.getClickCount() == 1 && row != -1) {
                        col = table.columnAtPoint(e.getPoint());
                        Maneuver m = model.getManeuver(row);
                        pE.updateSelected(m);
                        prevSelectedRow = row;
                }
            }
        });

        //Add Keybinding
        table.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK),"copy");
        Action copy = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (row != -1 || col != -1) {
                    String value = (String) model.getValueAt(row, col);
                    StringSelection selection = new StringSelection(value);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);
                }
            }
        };
        table.getActionMap().put("copy", copy);

        //Set prefered size
        table.setPreferredScrollableViewportSize(new Dimension(700, 80));
    }

    private int getRowFromManeuver(Maneuver man) {
        return model.getManeuverIndex(man);
    }

    public void setSelectedManeuver(Maneuver man) {
        selectedManeuver = man;
        int row = getRowFromManeuver(selectedManeuver);

        if (row != -1) {
            table.setRowSelectionInterval(row, row);
            prevSelectedRow = row;
        }
    }

    public void updatePlan(PlanType plan) {
        model.updateTable(plan);

        if (prevSelectedRow != -1 && prevSelectedRow < model.getRowCount())
            table.setRowSelectionInterval(prevSelectedRow, prevSelectedRow);

    }

    public void reset() {
        selectedManeuver = null;
        prevSelectedRow = -1;
        table.clearSelection();
    }
}
