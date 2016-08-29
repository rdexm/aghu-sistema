package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.paciente.vo.DescricaoCodigoComplementoCidVO;
import br.gov.mec.aghu.paciente.vo.QtdSessoesTratamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ImagemModalidadeOrientacaoVO;

public class RelatorioTratamentoFisiatricoVO implements Serializable {

	private static final long serialVersionUID = 5735624582373673413L;

	private List<ImagemModalidadeOrientacaoVO> listaImagemModalidadeOrientacaoVO;
	
	private List<DescricaoCodigoComplementoCidVO> listaDescricaoCodigoComplementoCidVO;
	
	private QtdSessoesTratamentoVO qtdSessoesTratamentoVO;
	
	private MptPrescricaoPaciente dataPrevExecucaoTratamentoObservacao;
	
	private MptPrescricaoPaciente prescricaoPaciente;
	
	private AipPacientes aipPacientes;
	
	private String localizacaoPaciente;
	
	private String prontuarioFormatado;
	
	private String responsavelFormatado;

	public List<ImagemModalidadeOrientacaoVO> getListaImagemModalidadeOrientacaoVO() {
		return listaImagemModalidadeOrientacaoVO;
	}

	public void setListaImagemModalidadeOrientacaoVO(
			List<ImagemModalidadeOrientacaoVO> listaImagemModalidadeOrientacaoVO) {
		this.listaImagemModalidadeOrientacaoVO = listaImagemModalidadeOrientacaoVO;
	}

	public List<DescricaoCodigoComplementoCidVO> getListaDescricaoCodigoComplementoCidVO() {
		return listaDescricaoCodigoComplementoCidVO;
	}

	public void setListaDescricaoCodigoComplementoCidVO(
			List<DescricaoCodigoComplementoCidVO> listaDescricaoCodigoComplementoCidVO) {
		this.listaDescricaoCodigoComplementoCidVO = listaDescricaoCodigoComplementoCidVO;
	}

	public QtdSessoesTratamentoVO getQtdSessoesTratamentoVO() {
		return qtdSessoesTratamentoVO;
	}

	public void setQtdSessoesTratamentoVO(
			QtdSessoesTratamentoVO qtdSessoesTratamentoVO) {
		this.qtdSessoesTratamentoVO = qtdSessoesTratamentoVO;
	}

	public MptPrescricaoPaciente getDataPrevExecucaoTratamentoObservacao() {
		return dataPrevExecucaoTratamentoObservacao;
	}

	public void setDataPrevExecucaoTratamentoObservacao(
			MptPrescricaoPaciente dataPrevExecucaoTratamentoObservacao) {
		this.dataPrevExecucaoTratamentoObservacao = dataPrevExecucaoTratamentoObservacao;
	}	

	public MptPrescricaoPaciente getPrescricaoPaciente() {
		return prescricaoPaciente;
	}

	public void setPrescricaoPaciente(MptPrescricaoPaciente prescricaoPaciente) {
		this.prescricaoPaciente = prescricaoPaciente;
	}

	public AipPacientes getAipPacientes() {
		return aipPacientes;
	}

	public void setAipPacientes(AipPacientes aipPacientes) {
		this.aipPacientes = aipPacientes;
	}

	public String getLocalizacaoPaciente() {
		return localizacaoPaciente;
	}

	public void setLocalizacaoPaciente(String localizacaoPaciente) {
		this.localizacaoPaciente = localizacaoPaciente;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public String getResponsavelFormatado() {
		return responsavelFormatado;
	}

	public void setResponsavelFormatado(String responsavelFormatado) {
		this.responsavelFormatado = responsavelFormatado;
	}	
	
}
