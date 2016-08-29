package br.gov.mec.aghu.transplante.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.model.MtxDoencaBases;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;


public class DoencaBaseCRUD extends ActionController {

	private static final long serialVersionUID = 1308195042096060091L;

	@EJB
	private ITransplanteFacade transplanteFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private MtxDoencaBases mtxDoencaBases = new MtxDoencaBases();
	private DominioTipoOrgao novoOrgaoDoencaBase;

	
	private static final String PAGE_PESQUISA_DOENCA_BASE = "transplante-doencaBase";
	private Boolean ativo;
	private Boolean edicaoAtiva = Boolean.FALSE;
	
	
	
	@PostConstruct
	public void init() {
		begin(conversation);	
	}

	public void iniciar() {
		if (edicaoAtiva) {
			if (this.mtxDoencaBases.getIndSituacao().isAtivo()) {
				this.ativo = true;
			} else {
				this.ativo = false;
			}
			novoOrgaoDoencaBase = mtxDoencaBases.getTipoOrgao();
			
		}else{
			this.setNovoOrgaoDoencaBase(DominioTipoOrgao.R); //Seta Rim como padrão ao iniciar a tela
		}
		
	}

	public String voltar() {
		edicaoAtiva = false;
		ativo = false;
		
		limparCadastro();
		return PAGE_PESQUISA_DOENCA_BASE;
	}

	public String gravar() {
		if(!StringUtils.isBlank(this.mtxDoencaBases.getDescricao())){
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			mtxDoencaBases.setTipoOrgao(this.novoOrgaoDoencaBase);
			mtxDoencaBases.setDescricao(StringUtil.trim(this.mtxDoencaBases.getDescricao()));
			mtxDoencaBases.setServidor(servidorLogado);
			mtxDoencaBases.setCriadoEm(new Date()); 
			if (edicaoAtiva) {
				atualizar();				
			} else {
				adicionar();				
			}
		}else{
			apresentarMsgNegocio(Severity.FATAL, "MSG_ERRO_DESCRICAO_BRANCO");
		}
		return null;
	}

	public String atualizar() {
		Boolean resultadoatualizao= Boolean.FALSE;

		try {
			mtxDoencaBases.setIndSituacao(DominioSituacao.getInstance(ativo));
			resultadoatualizao = transplanteFacade.atualizarDoencaBase(mtxDoencaBases);
			if (resultadoatualizao) {
				apresentarMsgNegocio(Severity.INFO, "MSG_ATUALIZACAO_SUCESSO_MTX_DOENCAS");
				return voltar();
			}else{
				apresentarMsgNegocio(Severity.FATAL, "MSG_ERRO_ADICIONAR_MTX_DOENCAS");
			}
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.FATAL, "MSG_ERRO_ATUALIZACAO_MTX_DOENCAS", new Object[] { e.getMessage() });
		}
		return null;
	}

	public String adicionar() {
		Boolean resultadoInsercao= Boolean.FALSE;

		try {
			mtxDoencaBases.setIndSituacao(DominioSituacao.getInstance(ativo));
			resultadoInsercao = transplanteFacade.inserirDoencaBase(mtxDoencaBases);
			if (resultadoInsercao) {
				apresentarMsgNegocio(Severity.INFO, "MSG_GRAVAR_SUCESSO_MTX_DOENCAS");
				limparCadastro();
				return voltar();
			}else{
				apresentarMsgNegocio(Severity.FATAL, "MSG_ERRO_ADICIONAR_MTX_DOENCAS");
			}
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.FATAL, "MSG_ERRO_ADD_MTX_DOENCAS", new Object[] { e.getMessage() });
		}
		return null;
	}

	public void limparCadastro() {
		ativo = false;
		this.setNovoOrgaoDoencaBase(DominioTipoOrgao.R); //Seta Rim como padrão ao iniciar a tela
		mtxDoencaBases = new MtxDoencaBases();
	}

	public MtxDoencaBases getMtxDoencaBases() {
		return mtxDoencaBases;
	}

	public void setMtxDoencaBases(MtxDoencaBases mtxDoencaBases) {
		this.mtxDoencaBases = mtxDoencaBases;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getEdicaoAtiva() {
		return edicaoAtiva;
	}

	public void setEdicaoAtiva(Boolean edicaoAtiva) {
		this.edicaoAtiva = edicaoAtiva;

	}

	public DominioTipoOrgao getNovoOrgaoDoencaBase() {
		return novoOrgaoDoencaBase;
	}

	public void setNovoOrgaoDoencaBase(DominioTipoOrgao novoOrgaoDoencaBase) {
		this.novoOrgaoDoencaBase = novoOrgaoDoencaBase;
	}
}

	
