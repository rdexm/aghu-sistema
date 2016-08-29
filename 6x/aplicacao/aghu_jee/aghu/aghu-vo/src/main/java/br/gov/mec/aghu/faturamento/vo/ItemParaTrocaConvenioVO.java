package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatItemContaHospitalar;


public class ItemParaTrocaConvenioVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5023616923220398004L;

	private FatItemContaHospitalar itemContaInternacao;

	private FatContasHospitalares contaHospitalar;

	private AghAtendimentos atendimento;

	public ItemParaTrocaConvenioVO() {
	}

	public ItemParaTrocaConvenioVO(FatItemContaHospitalar itemContaInternacao, FatContasHospitalares contaHospitalar,
			AghAtendimentos atendimento) {
		this.itemContaInternacao = itemContaInternacao;
		this.contaHospitalar = contaHospitalar;
		this.atendimento = atendimento;
	}

	public FatItemContaHospitalar getItemContaInternacao() {
		return itemContaInternacao;
	}

	public void setItemContaInternacao(FatItemContaHospitalar itemContaInternacao) {
		this.itemContaInternacao = itemContaInternacao;
	}

	public FatContasHospitalares getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(FatContasHospitalares contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

}
