package br.gov.mec.aghu.blococirurgico.equipamento;

import java.util.List;

import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcEquipamentoNotaSala;
import br.gov.mec.aghu.model.MbcEquipamentoUtilCirg;

public class TelaEquipamentoVO {
	
	private MbcCirurgias cirurgia;
	private MbcEquipamentoNotaSala equipamentoNotaSala;
	private MbcEquipamentoUtilCirg equipamentoUtilCirg;
	private String localizacao;
	private List<MbcEquipamentoUtilCirg> listaMbcEquipamentoUtilCirg;
	private Integer crgSeqParaExclusao;
	private Integer euuSeqParaExclusao;
	private MbcEquipamentoCirurgico mbcEquipamentoCirurgicoSelecionadoNaSuggestion;
	private Short quantidade;

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}	

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public MbcEquipamentoNotaSala getEquipamentoNotaSala() {
		return equipamentoNotaSala;
	}

	public void setEquipamentoNotaSala(MbcEquipamentoNotaSala equipamentoNotaSala) {
		this.equipamentoNotaSala = equipamentoNotaSala;
	}

	public MbcEquipamentoUtilCirg getEquipamentoUtilCirg() {
		return equipamentoUtilCirg;
	}

	public void setEquipamentoUtilCirg(MbcEquipamentoUtilCirg equipamentoUtilCirg) {
		this.equipamentoUtilCirg = equipamentoUtilCirg;
	}	

	public List<MbcEquipamentoUtilCirg> getListaMbcEquipamentoUtilCirg() {
		return listaMbcEquipamentoUtilCirg;
	}

	public void setListaMbcEquipamentoUtilCirg(
			List<MbcEquipamentoUtilCirg> listaMbcEquipamentoUtilCirg) {
		this.listaMbcEquipamentoUtilCirg = listaMbcEquipamentoUtilCirg;
	}

	public Integer getCrgSeqParaExclusao() {
		return crgSeqParaExclusao;
	}

	public void setCrgSeqParaExclusao(Integer crgSeqParaExclusao) {
		this.crgSeqParaExclusao = crgSeqParaExclusao;
	}

	public Integer getEuuSeqParaExclusao() {
		return euuSeqParaExclusao;
	}

	public void setEuuSeqParaExclusao(Integer euuSeqParaExclusao) {
		this.euuSeqParaExclusao = euuSeqParaExclusao;
	}

	public MbcEquipamentoCirurgico getMbcEquipamentoCirurgicoSelecionadoNaSuggestion() {
		return mbcEquipamentoCirurgicoSelecionadoNaSuggestion;
	}

	public void setMbcEquipamentoCirurgicoSelecionadoNaSuggestion(
			MbcEquipamentoCirurgico mbcEquipamentoCirurgicoSelecionadoNaSuggestion) {
		this.mbcEquipamentoCirurgicoSelecionadoNaSuggestion = mbcEquipamentoCirurgicoSelecionadoNaSuggestion;
	}

	public Short getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}
}
