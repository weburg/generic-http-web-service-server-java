package beans;

import com.weburg.domain.Engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EnginesBean implements Serializable {
	private List<Engine> engines = new ArrayList<>();

	public EnginesBean() {}

	public List<Engine> getEngines() {
		return engines;
	}

	public void setEngines(List<Engine> engines) {
		this.engines = engines;
	}
}
