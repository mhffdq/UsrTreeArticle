package com.jr2jme.UsrTreeArticle;

import java.io.File;
import java.io.IOException;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

public class MakeSVG {
	public  void importgexf(String str){
		//Init a project - and therefore a workspace
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();
		//Import file
		ImportController importController = Lookup.getDefault().lookup(ImportController.class);
		Container container;
		try {
			File file = new File(str+".gexf");
			container = importController.importFile(file);
			container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED); //Force DIRECTED
			container.setAllowAutoNode(false); //Don’t create missing nodes
			} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		//Append imported data to GraphAPI
		importController.process(container, new DefaultProcessor(), workspace);
		//Get a graph model - it exists because we have a workspace
		GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
		//Run YifanHuLayout for 100 passes - The layout always takes the current visible view
		ForceAtlasLayout layout = new ForceAtlasLayout(null);
		layout.setGraphModel(graphModel);
		layout.resetPropertiesValues();
		layout.setAttractionStrength(1D);
		layout.initAlgo();
		for (int i = 0; i < 1000 && layout.canAlgo(); i++) {
			layout.goAlgo();
		}

		PreviewModel model = Lookup.getDefault().lookup(PreviewController.class).getModel();//preview
		model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
		model.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, model.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(3F));
		model.getProperties().putValue(PreviewProperty.SHOW_EDGE_LABELS, Boolean.TRUE);
		model.getProperties().putValue(PreviewProperty.EDGE_CURVED,Boolean.FALSE);
		model.getProperties().putValue(PreviewProperty.EDGE_LABEL_FONT, model.getProperties().getFontValue(PreviewProperty.EDGE_LABEL_FONT).deriveFont(3F));

		//Simple PDF export
		ExportController ec = Lookup.getDefault().lookup(ExportController.class);
		try {
			ec.exportFile(new File(str+".svg"));
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}

	}

}
