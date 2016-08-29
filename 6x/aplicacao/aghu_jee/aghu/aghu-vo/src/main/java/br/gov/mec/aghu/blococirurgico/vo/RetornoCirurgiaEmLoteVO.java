package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcSalaCirurgica;

public class RetornoCirurgiaEmLoteVO implements Serializable {
	
	private static final long serialVersionUID = 7706845294261905001L;
	
	private AghUnidadesFuncionais aghUnidadesFuncionaisSuggestionBox;
	private MbcSalaCirurgica mbcSalaCirurgicaSuggestionBox;
	private MbcCirurgias cirurgia;
	private Integer prontuario;
	private Date dataCirurgia;
	private List<MbcCirurgias> listaMbcCirurgiaPesquisada;
	private List<RetornoCirurgiaEmLotePesquisaVO> listaBind;
	private Boolean mostrarSlidersAposPesquisa;
	private DominioSituacao situacao;
	public Short convenioId;
	public Byte planoId;
	public Integer cirurgiaSeqSelecionada;
	public String nomeResponsavelCirurgia;
	public List<String> tipoAnestesiaDescricoes;
	public List<MbcProcEspPorCirurgiasVO> listaMbcProcEspPorCirurgiasVO;
	private RetornoCirurgiaEmLotePesquisaVO cirurgiaParaEditar;
	private FatConvenioSaudePlano plano;
	private List<String> listaResponsavel;
	private List<MbcAnestesiaCirurgias> listaAnestesia;
	private List<MbcProcEspPorCirurgiasVO> procedimentos;
	private DominioSituacaoCirurgia dominioAntesMudar;
	public Boolean mostrarBotoesEdicao;
	private RetornoCirurgiaEmLotePesquisaVO cirurgiaSelecionada;
	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public AghUnidadesFuncionais getAghUnidadesFuncionaisSuggestionBox() {
		return aghUnidadesFuncionaisSuggestionBox;
	}

	public void setAghUnidadesFuncionaisSuggestionBox(
			AghUnidadesFuncionais aghUnidadesFuncionaisSuggestionBox) {
		this.aghUnidadesFuncionaisSuggestionBox = aghUnidadesFuncionaisSuggestionBox;
	}

	public MbcSalaCirurgica getMbcSalaCirurgicaSuggestionBox() {
		return mbcSalaCirurgicaSuggestionBox;
	}

	public void setMbcSalaCirurgicaSuggestionBox(
			MbcSalaCirurgica mbcSalaCirurgicaSuggestionBox) {
		this.mbcSalaCirurgicaSuggestionBox = mbcSalaCirurgicaSuggestionBox;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public MbcCirurgias getCirurgia() {
		if(this.cirurgia == null){
			this.cirurgia = new MbcCirurgias();
		}
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
		
	}

	public List<MbcCirurgias> getListaMbcCirurgiaPesquisada() {
		return listaMbcCirurgiaPesquisada;
	}

	public void setListaMbcCirurgiaPesquisada(
			List<MbcCirurgias> listaMbcCirurgiaPesquisada) {
		this.listaMbcCirurgiaPesquisada = listaMbcCirurgiaPesquisada;
	}

	public Boolean getMostrarSlidersAposPesquisa() {
		return mostrarSlidersAposPesquisa;
	}

	public void setMostrarSlidersAposPesquisa(Boolean mostrarSlidersAposPesquisa) {
		this.mostrarSlidersAposPesquisa = mostrarSlidersAposPesquisa;
	}

	public List<RetornoCirurgiaEmLotePesquisaVO> getListaBind() {
		return listaBind;
	}

	public void setListaBind(List<RetornoCirurgiaEmLotePesquisaVO> listaBind) {
		this.listaBind = listaBind;
	}
	
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public void fazerBindDaListaPesquisada(){
		this.listaBind = new ArrayList<RetornoCirurgiaEmLotePesquisaVO>();
		for(MbcCirurgias cirurgia : this.listaMbcCirurgiaPesquisada){
			RetornoCirurgiaEmLotePesquisaVO bind = new RetornoCirurgiaEmLotePesquisaVO();
			bind.setCirurgiaSeq(cirurgia.getSeq());
			bind.setConvenio(cirurgia.getConvenioSaude().getCodigo());
			bind.setDataInicio(cirurgia.getDataInicioCirurgia());
			bind.setDataFim(cirurgia.getDataFimCirurgia());
			bind.setNomePaciente(cirurgia.getPaciente().getNome());
			bind.setPlano(cirurgia.getConvenioSaudePlano());
			bind.setProntuario(cirurgia.getPaciente().getProntuario());
			bind.setSala(cirurgia.getSalaCirurgica().getId().getSeqp());
			bind.setSituacao(cirurgia.getSituacao());
			
			listaBind.add(bind);
		}
	}

	public Short getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	public Byte getPlanoId() {
		return planoId;
	}

	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}

	public FatConvenioSaudePlano getPlano() {
		return plano;
	}

	public void setPlano(FatConvenioSaudePlano plano) {
		this.plano = plano;
	}

	public Integer getCirurgiaSeqSelecionada() {
		return cirurgiaSeqSelecionada;
	}

	public void setCirurgiaSeqSelecionada(Integer cirurgiaSeqSelecionada) {
		this.cirurgiaSeqSelecionada = cirurgiaSeqSelecionada;
	}

	public String getNomeResponsavelCirurgia() {
		return nomeResponsavelCirurgia;
	}

	public void setNomeResponsavelCirurgia(String nomeResponsavelCirurgia) {
		this.nomeResponsavelCirurgia = nomeResponsavelCirurgia;
	}

	public List<String> getTipoAnestesiaDescricoes() {
		return tipoAnestesiaDescricoes;
	}

	public void setTipoAnestesiaDescricoes(List<MbcAnestesiaCirurgias> listMbcAnestesiaCirurgias) {
		if(this.tipoAnestesiaDescricoes == null){
			this.tipoAnestesiaDescricoes = new ArrayList<String>();
			for (MbcAnestesiaCirurgias bind : listMbcAnestesiaCirurgias) {
				this.tipoAnestesiaDescricoes.add(bind.getMbcTipoAnestesias().getDescricao());
			}
		}
	}

	public List<MbcProcEspPorCirurgiasVO> getListaMbcProcEspPorCirurgiasVO() {
		return listaMbcProcEspPorCirurgiasVO;
	}

	public void setListaMbcProcEspPorCirurgiasVO(List<MbcProcEspPorCirurgiasVO> listaMbcProcEspPorCirurgiasVO) {
		this.listaMbcProcEspPorCirurgiasVO = listaMbcProcEspPorCirurgiasVO;
	}

	public RetornoCirurgiaEmLotePesquisaVO getCirurgiaParaEditar() {
		return cirurgiaParaEditar;
	}

	public void setCirurgiaParaEditar(
			RetornoCirurgiaEmLotePesquisaVO cirurgiaParaEditar) {
		this.cirurgiaParaEditar = cirurgiaParaEditar;
	}	

	public List<MbcAnestesiaCirurgias> getListaAnestesia() {
		return listaAnestesia;
	}

	public void setListaAnestesia(List<MbcAnestesiaCirurgias> listaAnestesia) {
		this.listaAnestesia = listaAnestesia;
	}

	public List<MbcProcEspPorCirurgiasVO> getProcedimentos() {
		return procedimentos;
	}

	public void setProcedimentos(List<MbcProcEspPorCirurgiasVO> procedimentos) {
		this.procedimentos = procedimentos;
	}

	public List<String> getListaResponsavel() {
		if(listaResponsavel == null){
			listaResponsavel = new ArrayList<String>();
		}
		return listaResponsavel;
	}

	public void setListaResponsavel(List<String> listaResponsavel) {
		this.listaResponsavel = listaResponsavel;
	}

	public void adicionarNovoNomeResponsavelCirurgia(String obterNomePessoaServidor) {
		if(this.listaResponsavel == null){
			this.listaResponsavel = new ArrayList<String>();
			this.listaResponsavel.add(obterNomePessoaServidor);
		} else if(this.listaResponsavel.size() > 0){
			this.listaResponsavel.set(0, obterNomePessoaServidor);
		} else {
			this.listaResponsavel.add(obterNomePessoaServidor);
		}
	}

	public DominioSituacaoCirurgia getDominioAntesMudar() {
		return dominioAntesMudar;
	}

	public void setDominioAntesMudar(DominioSituacaoCirurgia dominioAntesMudar) {
		this.dominioAntesMudar = dominioAntesMudar;
	}

	public Boolean getMostrarBotoesEdicao() {
		return mostrarBotoesEdicao;
	}

	public void setMostrarBotoesEdicao(Boolean mostrarBotoesEdicao) {
		this.mostrarBotoesEdicao = mostrarBotoesEdicao;
	}

	public RetornoCirurgiaEmLotePesquisaVO getCirurgiaSelecionada() {
		return cirurgiaSelecionada;
	}

	public void setCirurgiaSelecionada(
			RetornoCirurgiaEmLotePesquisaVO cirurgiaSelecionada) {
		this.cirurgiaSelecionada = cirurgiaSelecionada;
	}
}
