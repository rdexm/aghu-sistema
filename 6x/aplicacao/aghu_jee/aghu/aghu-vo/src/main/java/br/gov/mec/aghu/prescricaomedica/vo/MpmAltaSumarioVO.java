package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.model.MpmAltaSumarioId;

/**
 * Classe utilizado para recuperar dados da Base.<br>
 * Utilizada para verificacoes apenas.<br>
 *  
 * @author rcorvalao
 *
 */
public class MpmAltaSumarioVO {
	
	private MpmAltaSumarioId id;
	
	private DominioIndConcluido concluido;
	private Date dthrAlta;
	private Boolean transfConcluida;
	
	public MpmAltaSumarioId getId() {
		return id;
	}
	public void setId(MpmAltaSumarioId id) {
		this.id = id;
	}
	public DominioIndConcluido getConcluido() {
		return concluido;
	}
	public void setConcluido(DominioIndConcluido concluido) {
		this.concluido = concluido;
	}
	public Date getDthrAlta() {
		return dthrAlta;
	}
	public void setDthrAlta(Date dthrAlta) {
		this.dthrAlta = dthrAlta;
	}
	public Boolean getTransfConcluida() {
		return transfConcluida;
	}
	public void setTransfConcluida(Boolean transfConcluida) {
		this.transfConcluida = transfConcluida;
	}
	
}
