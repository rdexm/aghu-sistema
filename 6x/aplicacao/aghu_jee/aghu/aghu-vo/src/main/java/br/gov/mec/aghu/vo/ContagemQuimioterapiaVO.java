package br.gov.mec.aghu.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ContagemQuimioterapiaVO implements BaseBean{

	private static final long serialVersionUID = 6726292813401857589L;
	
	private Integer atdPaciente;
	private Integer paciente;
	private Integer tratamentoTerapeuticos;
	private Short unidade;
	private Integer centroCusto;
	private Integer phi;
	private Integer ocv;
	private Date dtPrevExecucao;
	private Integer cuidado;
	private Integer freqAprazamento;
	private Short frequencia;
	private Integer quantidadeUnidade;
	private Integer unMedia;
	private Long qtdCuidados;
	
	public ContagemQuimioterapiaVO() {}

	public ContagemQuimioterapiaVO(Integer atdPaciente, Integer paciente,
			Integer tratamentoTerapeuticos, Short unidade,
			Integer centroCusto, Integer phi, Integer ocv, Date dtPrevExecucao,
			Integer cuidado, Integer freqAprazamento, Short frequencia,
			Integer quantidadeUnidade, Integer unMedia, Long qtdCuidados) {
		super();
		this.atdPaciente = atdPaciente;
		this.paciente = paciente;
		this.tratamentoTerapeuticos = tratamentoTerapeuticos;
		this.unidade = unidade;
		this.centroCusto = centroCusto;
		this.phi = phi;
		this.ocv = ocv;
		this.dtPrevExecucao = dtPrevExecucao;
		this.cuidado = cuidado;
		this.freqAprazamento = freqAprazamento;
		this.frequencia = frequencia;
		this.quantidadeUnidade = quantidadeUnidade;
		this.unMedia = unMedia;
		this.qtdCuidados = qtdCuidados;
	}

	public ContagemQuimioterapiaVO(Object[] objetos) {
		
		if(objetos[0]!= null){
			this.setAtdPaciente(Integer.valueOf(objetos[0].toString()));
		}
		if(objetos[1]!= null){
			this.setPaciente(Integer.valueOf(objetos[1].toString()));
		}
		if(objetos[2]!= null){
			this.setTratamentoTerapeuticos(Integer.valueOf(objetos[2].toString()));
		}
		if(objetos[3]!= null){
			this.setUnidade(Short.valueOf(objetos[3].toString()));
		}
		if(objetos[4]!= null){
			this.setCentroCusto(Integer.valueOf(objetos[4].toString()));
		}
		if(objetos[5]!= null){
			this.setPhi(Integer.valueOf(objetos[5].toString()));
		}
		if(objetos[6]!= null){
			this.setOcv(Integer.valueOf(objetos[6].toString()));
		}
		if(objetos[7]!= null){
			this.setDtPrevExecucao((Date)objetos[7]);
		}
		if(objetos[8]!= null){
			this.setCuidado(Integer.valueOf(objetos[8].toString()));
		}
		if(objetos[9]!= null){
			this.setFreqAprazamento(Integer.valueOf(objetos[9].toString()));
		}
		if(objetos[10]!= null){
			this.setFrequencia(Short.valueOf(objetos[10].toString()));
		}
		if(objetos[11]!= null){
			this.setQuantidadeUnidade(Integer.valueOf(objetos[11].toString()));
		}
		if(objetos[12]!= null){
			this.setUnMedia(Integer.valueOf(objetos[12].toString()));
		}
		if(objetos[13]!= null){
			this.setQtdCuidados(Long.valueOf(objetos[13].toString()));	
		}
	}
	
	public Integer getAtdPaciente() {
		return atdPaciente;
	}

	public Integer getPaciente() {
		return paciente;
	}

	public Integer getTratamentoTerapeuticos() {
		return tratamentoTerapeuticos;
	}

	public Short getUnidade() {
		return unidade;
	}

	public Integer getCentroCusto() {
		return centroCusto;
	}

	public Integer getPhi() {
		return phi;
	}

	public Integer getOcv() {
		return ocv;
	}

	public Date getDtPrevExecucao() {
		return dtPrevExecucao;
	}

	public Integer getCuidado() {
		return cuidado;
	}

	public Integer getFreqAprazamento() {
		return freqAprazamento;
	}

	public Short getFrequencia() {
		return frequencia;
	}

	public Integer getQuantidadeUnidade() {
		return quantidadeUnidade;
	}

	public Integer getUnMedia() {
		return unMedia;
	}

	public Long getQtdCuidados() {
		return qtdCuidados;
	}

	public void setAtdPaciente(Integer atdPaciente) {
		this.atdPaciente = atdPaciente;
	}

	public void setPaciente(Integer paciente) {
		this.paciente = paciente;
	}

	public void setTratamentoTerapeuticos(Integer tratamentoTerapeuticos) {
		this.tratamentoTerapeuticos = tratamentoTerapeuticos;
	}

	public void setUnidade(Short unidade) {
		this.unidade = unidade;
	}

	public void setCentroCusto(Integer centroCusto) {
		this.centroCusto = centroCusto;
	}

	public void setPhi(Integer phi) {
		this.phi = phi;
	}

	public void setOcv(Integer ocv) {
		this.ocv = ocv;
	}

	public void setDtPrevExecucao(Date dtPrevExecucao) {
		this.dtPrevExecucao = dtPrevExecucao;
	}

	public void setCuidado(Integer cuidado) {
		this.cuidado = cuidado;
	}

	public void setFreqAprazamento(Integer freqAprazamento) {
		this.freqAprazamento = freqAprazamento;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	public void setQuantidadeUnidade(Integer quantidadeUnidade) {
		this.quantidadeUnidade = quantidadeUnidade;
	}

	public void setUnMedia(Integer unMedia) {
		this.unMedia = unMedia;
	}

	public void setQtdCuidados(Long qtdCuidados) {
		this.qtdCuidados = qtdCuidados;
	}
	
}
