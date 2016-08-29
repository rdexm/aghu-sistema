package br.gov.mec.aghu.vo;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class RelatorioComGraficoVO implements Serializable {
	
	private static final long serialVersionUID = -6561677107438836012L;

	private String label;
	
	private BufferedImage graphic;
	
	public RelatorioComGraficoVO() {
	}
	
	public RelatorioComGraficoVO(String label, BufferedImage graphic) {
		this.label = label;
		this.graphic = graphic;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public BufferedImage getGraphic() {
		return graphic;
	}

	public void setGraphic(BufferedImage graphic) {
		this.graphic = graphic;
	}
	
}
