package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.exames.solicitacao.vo.DetalharItemSolicitacaoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

/**
 * @HIST ListaDesenhosMascarasExamesHistVO
 *
 */
public class ListaDesenhosMascarasExamesVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7770746099697345330L;
	private List<LinhaReportVO> notasAdicionais = new ArrayList<LinhaReportVO>();
	private List<DesenhoMascaraExameVO> desenhosMascarasExamesVO = new ArrayList<DesenhoMascaraExameVO>();
	private Integer soeSeq;
	private Short iseSeqp;

	private DetalharItemSolicitacaoPacienteVO pacienteVO;
	
	public List<LinhaReportVO> getNotasAdicionais() {
		return notasAdicionais;
	}
	public void setNotasAdicionais(List<LinhaReportVO> notasAdicionais) {
		this.notasAdicionais = notasAdicionais;
	}
	public List<DesenhoMascaraExameVO> getDesenhosMascarasExamesVO() {
		return desenhosMascarasExamesVO;
	}
	public void setDesenhosMascarasExamesVO(
			List<DesenhoMascaraExameVO> desenhosMascarasExamesVO) {
		this.desenhosMascarasExamesVO = desenhosMascarasExamesVO;
	}
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Short getIseSeqp() {
		return iseSeqp;
	}
	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}
	public DetalharItemSolicitacaoPacienteVO getPacienteVO() {
		return pacienteVO;
	}
	public void setPacienteVO(DetalharItemSolicitacaoPacienteVO pacienteVO) {
		this.pacienteVO = pacienteVO;
	}
}