package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.Element;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.modelsupport.modeltraversal.TraverseWorkspace;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.osate.ui.actions.AaxlReadOnlyActionAsJob;
import org.osgi.framework.Bundle;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;

public final class DoModelStatistics extends AaxlReadOnlyActionAsJob {
	private final STGroup javaSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/java.stg");
	private final STGroup midas_compsigSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/midas-compsig.stg");
	private final STGroup midas_appspecSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/midas-appspec.stg");

	protected Bundle getBundle() {
		return ArchitecturePlugin.getDefault().getBundle();
	}

	protected String getMarkerType() {
		return "org.osate.analysis.architecture.ModelStatisticsObjectMarker";
	}

	protected String getActionName() {
		return "Model statistics";
	}

	public void doAaxlAction(IProgressMonitor monitor, Element obj) {
		/*
		 * Doesn't make sense to set the number of work units, because the whole
		 * point of this action is count the number of elements. To set the work
		 * units we would effectively have to count everything twice.
		 */
		monitor.beginTask("Gathering model statistics",
				IProgressMonitor.UNKNOWN);
		// // Get the root object of the model
		// Element root = obj.getElementRoot();
		//
		// // Get the system instance (if any)
		// SystemInstance si;
		// if (obj instanceof InstanceObject)
		// si = ((InstanceObject) obj).getSystemInstance();
		// else
		// si = null;

		/**
		 * Examples of using the Index to look up a specific package,
		 * classifier, property, etc. In this case any scoping rules based on
		 * with clauses or project dependencies are ignored
		 */

		// Element e =
		// EMFIndexRetrieval.getPropertyDefinitionInWorkspace("Deadline");
		// Element p = EMFIndexRetrieval.getPackageInWorkspace("mydata::dd");
		// Element c =
		// EMFIndexRetrieval.getClassifierInWorkspace("mydata::dd::sys");

		/**
		 * Example of using the Index to get all classifiers In this case we
		 * then call on the resolver for the reference (causing the classifier
		 * to be loaded)
		 */

		// EList<IEObjectDescription> classifierlist =
		// EMFIndexRetrieval.getAllClassifiersInWorkspace();
		// for (IEObjectDescription cleod : classifierlist){
		// Classifier cl = (Classifier)
		// EcoreUtil.resolve(cleod.getEObjectOrProxy(),
		// OsateResourceUtil.getResourceSet());//obj.eResource().getResourceSet());
		// stats.process(cl);
		// }

		/**
		 * Example of counting without causing the classifier to load
		 */

		// EList<IEObjectDescription> classifierlist1 =
		// EMFIndexRetrieval.getAllClassifiersInWorkspace();
		// Resource res = obj.eResource();
		// for (IEObjectDescription cleod : classifierlist1){
		// stats.countClassifier(cleod.getEClass());
		// }

		/*
		 * Create a new model statistics analysis object and run it over the
		 * declarative model. If an instance model exists, run it over that too.
		 */
		ModelStatistics stats = new ModelStatistics(monitor, getErrorManager());
		/*
		 * Accumulate the results in a StringBuffer, but also report them using
		 * info markers attached to the root model object.
		 */
		// run statistics on all declarative models in the workspace

		// stats.defaultTraversalAllDeclarativeModels();
		// stats.processPreOrderWithLeavesAll();
		// stats.processPostOrderAll();
		// System.out.println("=====");
		// stats.processPreOrderAll();

		// TODO: This is pretty ugly. It works as a testing rig, but it should
		// probably get cleaned up considerably before any sort of release
		
		// The system _has_ to come first, so we make sure it's first
		ResourceSet rs = OsateResourceUtil.createResourceSet();
		HashSet<IFile> files = TraverseWorkspace
				.getAadlandInstanceFilesInWorkspace();
		LinkedList<IFile> fileList = new LinkedList<>();
		for (IFile f : files) {
			Resource res = rs.getResource(
					OsateResourceUtil.getResourceURI((IResource) f), true);

			Element target = (Element) res.getContents().get(0);
			if (!(target instanceof AadlPackage))
				continue;
			AadlPackage pack = (AadlPackage) target;
			PublicPackageSection sect = pack.getPublicSection();
			Classifier ownedClassifier = sect.getOwnedClassifiers().get(0);
			if ((ownedClassifier instanceof org.osate.aadl2.System)) {
				fileList.addFirst(f);
			} else {
				fileList.addLast(f);
			}
		}

		// Now we process all the files, with the system first.
		for (IFile f : fileList) {
			Resource res = rs.getResource(
					OsateResourceUtil.getResourceURI((IResource) f), true);
			Element target = (Element) res.getContents().get(0);
			stats.process(target);
		}

		// final StringBuffer msg = new StringBuffer();

		// if (si != null) {
		// stats.defaultTraversal(si);
		// }

		midas_compsigSTG.delimiterStartChar = '$';
		midas_compsigSTG.delimiterStopChar = '$';
		for (ProcessModel pm : stats.getSystemModel().getLogicComponents()
				.values()) {
			System.out.println(javaSTG.getInstanceOf("class").add("model", pm)
					.render());
			System.out.println(midas_compsigSTG.getInstanceOf("compsig")
					.add("model", pm).render());
		}
		midas_appspecSTG.delimiterStartChar = '$';
		midas_appspecSTG.delimiterStopChar = '$';

		System.out.println(midas_appspecSTG.getInstanceOf("appspec")
				.add("model", stats.getSystemModel()).render());
		monitor.done();
	}
}
