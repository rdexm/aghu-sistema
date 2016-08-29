package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.model.MbcAgendaDiagnostico;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class VisualizaCirurgiaCanceladaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3478488731034673346L;
	
	private Integer prontuario;
	private Integer pacCodigo;
	private String pacNome;
	private Date dataCancelamento;
	private Integer numeroCancelamentos;
	private String motivoCancelamento;
	private DominioRegimeProcedimentoCirurgicoSus regime;
	private Date tempo;
	private String procedimentoPrincipal;
	private String comentario;
	
	private List<MbcAgendaAnestesia> listaAnestesias;
	private List<MbcAgendaProcedimento> listaProcedimentos;
	private MbcAgendaDiagnostico diagnostico;
	
	
	//Getters and Setters
	
	public String getProntuario() {
		return CoreUtil.formataProntuario(prontuario);
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public String getPacNome() {
		return pacNome;
	}
	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}
	public Date getDataCancelamento() {
		return dataCancelamento;
	}
	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}
	public Integer getNumeroCancelamentos() {
		return numeroCancelamentos;
	}
	public void setNumeroCancelamentos(Integer numeroCancelamentos) {
		this.numeroCancelamentos = numeroCancelamentos;
	}
	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}
	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}
	public String getRegime() {
		return regime.getDescricao();
	}
	public void setRegime(DominioRegimeProcedimentoCirurgicoSus regime) {
		this.regime = regime;
	}
	public Date getTempo() {
		return tempo;
	}
	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}
	public String getProcedimentoPrincipal() {
		return procedimentoPrincipal;
	}
	public void setProcedimentoPrincipal(String procedimentoPrincipal) {
		this.procedimentoPrincipal = procedimentoPrincipal;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public List<MbcAgendaAnestesia> getListaAnestesias() {
		return listaAnestesias;
	}
	public void setListaAnestesias(List<MbcAgendaAnestesia> listaAnestesias) {
		this.listaAnestesias = listaAnestesias;
	}
	public List<MbcAgendaProcedimento> getListaProcedimentos() {
		return listaProcedimentos;
	}
	public void setListaProcedimentos(List<MbcAgendaProcedimento> listaProcedimentos) {
		this.listaProcedimentos = listaProcedimentos;
	}
	public MbcAgendaDiagnostico getDiagnostico() {
		return diagnostico;
	}
	public void setDiagnostico(MbcAgendaDiagnostico diagnostico) {
		this.diagnostico = diagnostico;
	}
		
}
