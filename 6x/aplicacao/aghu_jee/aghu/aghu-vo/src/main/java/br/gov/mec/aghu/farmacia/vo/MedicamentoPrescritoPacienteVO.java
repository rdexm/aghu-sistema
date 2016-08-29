package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

/**
 * Os dados armazenados nesse objeto representam os Medicamentos Prescritos por Paciente
 * 
 * @author Sedimar
 */

public class MedicamentoPrescritoPacienteVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2952759177759165483L;
	//Parametros da busca para exibir no relatorio
	private String dataReferenciaInicio;
	private String dataReferenciaFim;
	
	private String medicamentoDescricaoCompleta;
	private String unfAndarAlaDescricaoSigla;
	private String nomePaciente;
	private String prontuarioPaciente;
	private String qtdeDispensada;
	private BigDecimal qtdeDispensada1;
	private String indSituacao;
	
	//GETTERS E SETTERS

	public Integer getProntuarioPacienteInt() {
		Integer prontuario = Integer.parseInt(this.prontuarioPaciente);
		return prontuario;
	}
	
	public String getMedicamentoDescricaoCompleta() {
		return medicamentoDescricaoCompleta;
	}

	public void setMedicamentoDescricaoCompleta(String medicamentoDescricaoCompleta) {
		this.medicamentoDescricaoCompleta = medicamentoDescricaoCompleta;
	}

	//MED.DESCRICAO||' '||decode(MED.CONCENTRACAO,null,null,MED.CONCENTRACAO||' '||umm.descricao) DESCRICAO1,
	public void setMedicamentoDescricaoCompleta(String descricao, String concentracao, String unidMedidaMedicaDescricao) {
		
		this.medicamentoDescricaoCompleta = descricao;
		if (StringUtils.isNotBlank(medicamentoDescricaoCompleta)) {
			if (concentracao != null) {
				this.medicamentoDescricaoCompleta = medicamentoDescricaoCompleta + " " + concentracao + " " + unidMedidaMedicaDescricao;
			}
		}	
	}

	public String getUnfAndarAlaDescricaoSigla() {
		return unfAndarAlaDescricaoSigla;
	}

	public void setUnfAndarAlaDescricaoSigla(String unfAndarAlaDescricaoSigla) {
		this.unfAndarAlaDescricaoSigla = unfAndarAlaDescricaoSigla;
	}
	
	//decode(unf.andar,0, unf.sigla||' '||unf.descricao,
	//                 1,unf.sigla||' '||unf.descricao, 
	// to_char(unf.andar)||' '||ind_ala||' '||unf.descricao) DESCRICAO2,
	public void setUnfAndarAlaDescricaoSigla(String andar, String ala, String descricao, String sigla) {
		if (andar != null) {
			if ("0".equals(andar) || "1".equals(andar)) {
				this.unfAndarAlaDescricaoSigla = sigla + " " + descricao;
			} else {
				this.unfAndarAlaDescricaoSigla = andar + " " + ala + " " + descricao;
			}
		}
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getProntuarioPaciente() {
		return prontuarioPaciente;
	}

	public void setProntuarioPaciente(String prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	public String getQtdeDispensada() {
		return qtdeDispensada;
	}

	public void setQtdeDispensada(String qtdeDispensada) {
		this.qtdeDispensada = qtdeDispensada;
	}

	public BigDecimal getQtdeDispensada1() {
		return qtdeDispensada1;
	}

	public void setQtdeDispensada1(BigDecimal qtdeDispensada1) {
		this.qtdeDispensada1 = qtdeDispensada1;
	}

	public String getDataReferenciaInicio() {
		return dataReferenciaInicio;
	}

	public void setDataReferenciaInicio(String dataReferenciaInicio) {
		this.dataReferenciaInicio = dataReferenciaInicio;
	}

	public String getDataReferenciaFim() {
		return dataReferenciaFim;
	}

	public void setDataReferenciaFim(String dataReferenciaFim) {
		this.dataReferenciaFim = dataReferenciaFim;
	}

	public String getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}
	
}
