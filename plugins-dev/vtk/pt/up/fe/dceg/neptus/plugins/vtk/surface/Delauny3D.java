/*
 * Copyright (c) 2004-2013 Universidade do Porto - Faculdade de Engenharia
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
 * https://www.lsts.pt/neptus/licence.
 *
 * For more information please see <http://lsts.fe.up.pt/neptus>.
 *
 * Author: hfq
 * May 9, 2013
 */
package pt.up.fe.dceg.neptus.plugins.vtk.surface;

import pt.up.fe.dceg.neptus.plugins.vtk.pointcloud.PointCloud;
import pt.up.fe.dceg.neptus.plugins.vtk.pointtypes.PointXYZ;
import vtk.vtkActor;
import vtk.vtkCleanPolyData;
import vtk.vtkDataSetMapper;
import vtk.vtkDelaunay3D;

/**
 * @author hfq
 *
 */
public class Delauny3D {

    public PointCloud<PointXYZ> pointCloud;
    
    private vtkActor delaunyActor;
    
    public Delauny3D(PointCloud<PointXYZ> pointCloud) {
        this.pointCloud = pointCloud;
 
    }

    /**
     * Performs a Delauny Triangulation from unorganized points
     * Uses the vtkDelauny3D filter ceates a tetrahedral mesh from unorganized points, that is a solid covex
     * hull of the original points.
     */
    public void performDelauny() {
        

            // clean the polydata. this will remove duplicate points that may be present in the input data  
        System.out.println("cleaning point cloud...");
        vtkCleanPolyData cleaner = new vtkCleanPolyData();
        cleaner.SetInputConnection(pointCloud.getPoly().GetProducerPort());
        //cleaner.SetInput(pointCloud.getPoly());
        
            // Generate a tetrahedral mesh from the input points. by default, the generated volume is the convex hull of the points       
        System.out.println("Generate mesh...");
        vtkDelaunay3D delauny3D = new vtkDelaunay3D();
        delauny3D.SetInputConnection(cleaner.GetOutputPort());
        //delauny3D.SetAlpha(0.1);
        
        System.out.println("setting mapper...");
        vtkDataSetMapper delaunyMapper = new vtkDataSetMapper();
        delaunyMapper.SetInputConnection(delauny3D.GetOutputPort());
        
        setDelaunyActor(new vtkActor());
        getDelaunyActor().SetMapper(delaunyMapper);
    }

    /**
     * @return the delaunyActor
     */
    public vtkActor getDelaunyActor() {
        return delaunyActor;
    }

    /**
     * @param delaunyActor the delaunyActor to set
     */
    public void setDelaunyActor(vtkActor delaunyActor) {
        this.delaunyActor = delaunyActor;
    }
    
}
