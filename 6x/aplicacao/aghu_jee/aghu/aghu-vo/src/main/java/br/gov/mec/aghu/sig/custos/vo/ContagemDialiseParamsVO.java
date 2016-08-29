package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.TratamentoTerapeuticoVO;

public class ContagemDialiseParamsVO implements Serializable {

	private static final long serialVersionUID = 1094758748075863219L;

	private SigProcessamentoCusto sigProcessamentoCusto; 
	private RapServidores rapServidores;	
	private SigProcessamentoPassos sigProcessamentoPassos;
	private CuidadosPrescricaoDialiseVO cuidadosPrescricaoDialise;
	private List<AghAtendimentos> atendimentosInternacaoList;
	private AinMovimentosInternacao ultimoMovimentoInternacao;
	private SigCalculoAtdPaciente calculoAtdPaciente;
	private TratamentoTerapeuticoVO tratamentoTerapeuticoVO;
	private Long qutCuidados;

	public ContagemDialiseParamsVO(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, CuidadosPrescricaoDialiseVO cuidadosPrescricaoDialise){
		this.sigProcessamentoCusto = sigProcessamentoCusto;
		this.rapServidores = rapServidores;
		this.sigProcessamentoPassos = sigProcessamentoPassos;
		this.cuidadosPrescricaoDialise = cuidadosPrescricaoDialise;
	}
	
	public ContagemDialiseParamsVO(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos){
		this.sigProcessamentoCusto = sigProcessamentoCusto;
		this.rapServidores = rapServidores;
		this.sigProcessamentoPassos = sigProcessamentoPassos;
	}

	public SigProcessamentoCusto getSigProcessamentoCusto() {
		return sigProcessamentoCusto;
	}

	public void setSigProcessamentoCusto(SigProcessamentoCusto sigProcessamentoCusto) {
		this.sigProcessamentoCusto = sigProcessamentoCusto;
	}

	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public SigProcessamentoPassos getSigProcessamentoPassos() {
		return sigProcessamentoPassos;
	}

	public void setSigProcessamentoPassos(
			SigProcessamentoPassos sigProcessamentoPassos) {
		this.sigProcessamentoPassos = sigProcessamentoPassos;
	}

	public CuidadosPrescricaoDialiseVO getCuidadosPrescricaoDialise() {
		return cuidadosPrescricaoDialise;
	}

	public void setCuidadosPrescricaoDialise(CuidadosPrescricaoDialiseVO cuidadosPrescricaoDialise) {
		this.cuidadosPrescricaoDialise = cuidadosPrescricaoDialise;
	}

	public List<AghAtendimentos> getAtendimentosInternacaoList() {
		return atendimentosInternacaoList;
	}

	public void setAtendimentosInternacaoList(
			List<AghAtendimentos> atendimentosInternacaoList) {
		this.atendimentosInternacaoList = atendimentosInternacaoList;
	}

	public void setTratamentoTerapeuticoVO(TratamentoTerapeuticoVO tratamentoTerapeuticoVO) {
		this.tratamentoTerapeuticoVO = tratamentoTerapeuticoVO;
	}

	public TratamentoTerapeuticoVO getTratamentoTerapeuticoVO() {
		return tratamentoTerapeuticoVO;
	}

	public void setQutCuidados(Long qutCuidados) {
		this.qutCuidados = qutCuidados;
	}

	public Long getQutCuidados() {
		return qutCuidados;
	}

	public void setCalculoAtdPaciente(SigCalculoAtdPaciente calculoAtdPaciente) {
		this.calculoAtdPaciente = calculoAtdPaciente;
	}

	public SigCalculoAtdPaciente getCalculoAtdPaciente() {
		return calculoAtdPaciente;
	}

	public AinMovimentosInternacao getUltimoMovimentoInternacao() {
		return ultimoMovimentoInternacao;
	}

	public void setUltimoMovimentoInternacao(AinMovimentosInternacao ultimoMovimentoInternacao) {
		this.ultimoMovimentoInternacao = ultimoMovimentoInternacao;
	}

}
