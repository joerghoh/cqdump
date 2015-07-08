package de.joerghoh.cq5.examples.oak.services.orderedFolder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * A simple benchmark on repo performance, especially focussed on the handling
 * of large numbers of siblings
 * @author jhoh
 *
 */

@SlingServlet(paths="/bin/example/oak-benchmark")
public class BenchmarkServlet extends SlingSafeMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(BenchmarkServlet.class);
	
	
	private static final String BASEPATH = "/var/oak-benchmark";
	private static final String NT_SLING_ORDERED_FOLDER = "sling:OrderedFolder";
	private static final String NT_SLING_FOLDER = "sling:Folder";
	
	
	private static final int NUMBER_OF_NODES = 5000;
	
	private static final int OFFSET = 100;

	@Reference
	SlingRepository repo;
	
	
	protected void doGet (SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		Session session = request.getResourceResolver().adaptTo(Session.class);
		if (session != null) {
			try {
				runBenchmark (session, NT_SLING_FOLDER, writer);
				runBenchmark(session, NT_SLING_ORDERED_FOLDER, writer);
			} catch (RepositoryException e) {
				
				e.printStackTrace();
			} finally {
				try {
					deleteAllBenchmarkNodes(session);
				} catch (RepositoryException e) {
					
					e.printStackTrace();
				}
			}
			
		} else {
			LOG.error("Cannot adapt resource resolver to session");
		}
		
	}
	
	
	protected void runBenchmark (Session session, String nodeType, PrintWriter writer) throws RepositoryException {
		
		String benchmarkPath = BASEPATH + "/" + nodeType;
		StopWatch sw = new StopWatch();
		long offset = 0;
		
		writer.print("<h2>Starting benchmark for nodeType " + nodeType + "</h2>");
		sw.start();
		
		Node folder = createBenchmarkNodes(session, nodeType, benchmarkPath);
		offset = writeOutput (writer, offset,"Create nodes", sw);
		
		randomReadAccess (folder);
		offset = writeOutput (writer, offset,"random read access", sw);
		
		addAdditionalNodes(folder);
		offset = writeOutput (writer, offset,"add more nodes", sw);
		
		String msg = validateOrder(folder);
		offset = writeOutput (writer, offset,"validate order: " + msg, sw);
		
		sw.stop();
	}
	
	protected long writeOutput (PrintWriter writer, long offset, String message, StopWatch sw) {
		sw.split();
		long newOffset = sw.getSplitTime();
		long diff = newOffset - offset;
		writer.print("<p>" + message + ": "+ diff + " ms</p>");
		
		sw.unsplit();
		return newOffset;
	}
	
	
	// benchmark routines
	
	
	/**
	 * create the nodes starting with the names "OFFSET, OFFSET+1, ... OFFSET + NUMBER_OF_NODES",
	 * leaving a small gap (from the numbering perspective at the beginning)
	 * @param session
	 * @param nodeType
	 * @param path
	 * @return
	 * @throws RepositoryException
	 */
	protected Node createBenchmarkNodes (Session session, String nodeType, String path) throws RepositoryException {
		
		Node dir = JcrUtils.getOrCreateByPath(path, nodeType, session);
		int maxNumber = NUMBER_OF_NODES + OFFSET;
		
		for (int i=OFFSET;i< maxNumber; i++) {
			String nodeName = "" + i;
			Node n = dir.addNode(nodeName,"nt:unstructured");
			session.save();
			
		}
		return dir;
	}
	
	protected void deleteAllBenchmarkNodes (Session session) throws PathNotFoundException, RepositoryException {
		Node n = session.getNode(BASEPATH);
		n.remove();
		session.save();
	}
	
	
	/**
	 * 
	 * reads 500 randomly choosen nodes from this node
	 * @param parent 
	 * 
	 */
	protected void randomReadAccess (Node parent) throws PathNotFoundException, RepositoryException {
		
		Random random = new Random();
		
		for (int i=0;i<500; i++) {
			int r = random.nextInt (NUMBER_OF_NODES) + OFFSET;
			String name = "" + r;
			parent.getNode(name);
		}
	}
	
	/**
	 * Adds some more nodes with the node names "0, ... ,OFFSET-1"
	 * @param dir
	 * @throws ItemExistsException
	 * @throws PathNotFoundException
	 * @throws NoSuchNodeTypeException
	 * @throws LockException
	 * @throws VersionException
	 * @throws ConstraintViolationException
	 * @throws RepositoryException
	 */
	protected void addAdditionalNodes (Node dir) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException {
		for (int i=0;i< OFFSET; i++) {
			String nodeName = "" + i;
			Node n = dir.addNode(nodeName,"nt:unstructured");
			dir.getSession().save();
			
		}
	}
	
	protected String validateOrder(Node parent) throws RepositoryException {
		
		int temp = -1;
		int count = 0;
		NodeIterator iter = parent.getNodes();
		while (iter.hasNext()) {
			count++;
			String name = iter.nextNode().getName();
			int i = Integer.parseInt(name);
			if (i > temp) {
				temp = i;
			} else {
				// the previous result had a higher number
				String msg = String.format ("Got node %s before node %s (iterations = %s)", new Object[]{temp,i,count});
				return msg;
			}
		}
		return "ok";
	}
	
	
}
