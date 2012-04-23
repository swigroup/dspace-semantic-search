/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package gr.upatras.ceid.hpclab.owl;

import gr.upatras.ceid.hpclab.reasoner.SupportedReasoner;

/**
 * Helper class to provide a pointer or key to SemanticUnit objects.
 * 
 * @author kotsomit
 *
 */
public class SemanticUnitMapKey {
	String url;
	SupportedReasoner supportedReasoner;
	String sid;
	
	
	 SemanticUnitMapKey(String url, SupportedReasoner supportedReasoner, String sid) {
		this.url = url;
		this.supportedReasoner = supportedReasoner;
		this.sid = sid;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		//result = prime * result + ((sid == null) ? 0 : sid.hashCode());
		result = prime
				* result
				+ ((supportedReasoner == null) ? 0 : supportedReasoner
						.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SemanticUnitMapKey other = (SemanticUnitMapKey) obj;
		if (sid == null) {
			if (other.sid != null)
				return false;
		} else if (!sid.equals(other.sid)&&!sid.equals("*")) //session wildcard
			return false;
		if (supportedReasoner == null) {
			if (other.supportedReasoner != null)
				return false;
		} else if (!supportedReasoner.getName().
				equals(other.supportedReasoner.getName())&&!supportedReasoner.getName().
				equals("ANY")) //reasoner wildcard
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	


	
}