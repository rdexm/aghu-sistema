package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoMotivoAlteraSituacoes;
import br.gov.mec.aghu.model.MtxMotivoAlteraSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;

public class MotivoAlteracaoSituacaoCRUD extends ActionController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 762673806116555741L;
	
	private Boolean ativo = Boolean.TRUE;
	private Boolean edicaoAtiva = Boolean.FALSE;
	private MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao = new MtxMotivoAlteraSituacao();
	private static final String PAGE_PESQUISA_MOTIVO_ALTERACAO_SITUACAO = "transplante-motivoAlteracaoSituacao";
	private List<String> listaTipoTransplanteCad = new ArrayList <String>();
	private List<String> listaTipoTransplanteSelecionadoCad = new ArrayList <String>();
	
	@EJB
	private ITransplanteFacade transplanteFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void iniciar() {
		carregarListaTipoTransplante();
		if (edicaoAtiva) {
			if (this.mtxMotivoAlteraSituacao.getTipo().toString().equals("T")) {
				listaTipoTransplanteSelecionadoCad.add(DominioTipoMotivoAlteraSituacoes.M.getDescricao());
				listaTipoTransplanteSelecionadoCad.add(DominioTipoMotivoAlteraSituacoes.O.getDescricao());
			}else{
				listaTipoTransplanteSelecionadoCad.add(this.mtxMotivoAlteraSituacao.getTipo().getDescricao());
			}
			if(this.mtxMotivoAlteraSituacao.getIndicadorSituacao().isAtivo()){
				this.ativo = true;
			} else {
				this.ativo = false;
			}
		}else{
			ativo = true;
			listaTipoTransplanteSelecionadoCad.add(DominioTipoMotivoAlteraSituacoes.M.getDescricao());
		}
	}
		
	public String gravar() {
		if(!StringUtils.isBlank(this.mtxMotivoAlteraSituacao.getDescricao())){
			mtxMotivoAlteraSituacao.setTipo(DominioTipoMotivoAlteraSituacoes.getInstance(listaTipoTransplanteSelecionadoCad));
			if(mtxMotivoAlteraSituacao.getTipo() == null){
				apresentarMsgNegocio(Severity.FATAL, "MSG_TIPO_REGISTRO_OBRIGATORIO"); 
			}else{
				mtxMotivoAlteraSituacao.setDescricao(StringUtil.trim(this.mtxMotivoAlteraSituacao.getDescricao()));
				RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
				mtxMotivoAlteraSituacao.setServidor(servidorLogado);
				mtxMotivoAlteraSituacao.setCriadoEm(new Date()); 
				mtxMotivoAlteraSituacao.setIndicadorSituacao(DominioSituacao.getInstance(ativo));
				if (edicaoAtiva) {
					atualizar();
					return voltar();
				} else {
					adicionar();
					return voltar();
				}
			}	
		}else{
			apresentarMsgNegocio(Severity.FATAL, "MSG_ERRO_DESCRICAO_BRANCO_MAS");
		}
		return null;
	}
	
	public void adicionar() {
		try {
			this.transplanteFacade.inserirMotivoAlteraSituacao(mtxMotivoAlteraSituacao);
			apresentarMsgNegocio(Severity.INFO, "MSG_GRAVAR_SUCESSO_MTX_MAS");
			limparCadastro();
			} catch (Exception e) {
			apresentarMsgNegocio(Severity.FATAL, "MSG_ERRO_ADD_MTX_MAS", new Object[] { e.getMessage() });
		}
	}
	
	public void atualizar() {
		try {
			this.transplanteFacade.atualizarMotivoAlteraSituacao(mtxMotivoAlteraSituacao);
			apresentarMsgNegocio(Severity.INFO, "MSG_ATUALIZACAO_SUCESSO_MAS");
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.FATAL,"MSG_ERRO_ATUALIZACAO_MAS",	new Object[] { e.getMessage() });
		}
	}
	
	public String excluir(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao){
	if(this.transplanteFacade.excluirMotivoAlteraSituacao(mtxMotivoAlteraSituacao)){
		apresentarMsgNegocio(Severity.INFO,"MSG_EXCLUIR_SUCESSO_MAS");
	}else{
		apresentarMsgNegocio(Severity.FATAL,"MSG_ERRO_EXCLUIR_MAS");
	}
	return PAGE_PESQUISA_MOTIVO_ALTERACAO_SITUACAO;	
	}
	
	public String voltar() {
		edicaoAtiva = false;
		ativo = false;
		limparCadastro();
		return PAGE_PESQUISA_MOTIVO_ALTERACAO_SITUACAO;
	}
	
	public void limparCadastro() {
		ativo = false;
		mtxMotivoAlteraSituacao = new MtxMotivoAlteraSituacao();
		listaTipoTransplanteSelecionadoCad = new ArrayList <String>();
		listaTipoTransplanteCad = new ArrayList <String>();
		carregarListaTipoTransplante();
		listaTipoTransplanteSelecionadoCad.add(DominioTipoMotivoAlteraSituacoes.M.getDescricao());
	}
	
	private void carregarListaTipoTransplante(){
		listaTipoTransplanteSelecionadoCad = new ArrayList <String>();
		listaTipoTransplanteCad = new ArrayList <String>();
		listaTipoTransplanteCad.add(DominioTipoMotivoAlteraSituacoes.M.getDescricao());
        listaTipoTransplanteCad.add(DominioTipoMotivoAlteraSituacoes.O.getDescricao());
	}
	
//	public List<DominioTipoMotivoAlteraSituacoes> getListaTipoTransplanteSelecionadoCad() {
//		return listaTipoTransplanteSelecionadoCad;
//	}
//
//	public void setListaTipoTransplanteSelecionadoCad(List<DominioTipoMotivoAlteraSituacoes> listaTipoTransplanteSelecionado) {
//		this.listaTipoTransplanteSelecionadoCad = listaTipoTransplanteSelecionado;
//	}

	public List<String> getListaTipoTransplanteCad() {
		return listaTipoTransplanteCad;
	}
	public void setListaTipoTransplanteCad(List<String> listaTipoTransplanteCad) {
		this.listaTipoTransplanteCad = listaTipoTransplanteCad;
	}
	public List<String> getListaTipoTransplanteSelecionadoCad() {
		return listaTipoTransplanteSelecionadoCad;
	}
	public void setListaTipoTransplanteSelecionadoCad(List<String> listaTipoTransplanteSelecionadoCad) {
		this.listaTipoTransplanteSelecionadoCad = listaTipoTransplanteSelecionadoCad;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public MtxMotivoAlteraSituacao getMtxMotivoAlteraSituacao() {
		return mtxMotivoAlteraSituacao;
	}

	public void setMtxMotivoAlteraSituacao(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao) {
		this.mtxMotivoAlteraSituacao = mtxMotivoAlteraSituacao;
	}
	public Boolean getEdicaoAtiva() {
		return edicaoAtiva;
	}

	public void setEdicaoAtiva(Boolean edicaoAtiva) {
		this.edicaoAtiva = edicaoAtiva;
	}
}
