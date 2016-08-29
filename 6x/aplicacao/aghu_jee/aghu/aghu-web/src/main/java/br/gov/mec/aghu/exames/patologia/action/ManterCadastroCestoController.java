package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelCestoPatologia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterCadastroCestoController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ManterCadastroCestoController.class);

	private static final long serialVersionUID = 6274619599402686574L;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private boolean ativo;

	private Integer seq;
	
	private String descricao;
	
	private String sigla;
	
	private DominioSituacao situacao;
	
	private List<AelCestoPatologia> lista;
	
	private AelCestoPatologia selecionado;
	
	//Para Adicionar itens
	private AelCestoPatologia aelCestoPatologia;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		aelCestoPatologia = new AelCestoPatologia();
	}
	
	public void pesquisar() {
		lista = examesPatologiaFacade.pesquisarAelCestoPatologia(seq, descricao, sigla, situacao);
		aelCestoPatologia = new AelCestoPatologia(); 
		ativo = true;
	}
	
	public void limpar() {
		ativo = false;
		seq = null;
		descricao = null;
		sigla = null;
		situacao = null;
		lista = null;
		aelCestoPatologia = new AelCestoPatologia(); 
	}
	
	public void gravar() {
		try {
			if(aelCestoPatologia.getSeq() != null){
				examesPatologiaFacade.alterarAelCestoPatologia(aelCestoPatologia);
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_CESTO_MATERIAL_UPDATE_SUCESSO", aelCestoPatologia.getDescricao());
				
			} else {
				examesPatologiaFacade.inserirAelCestoPatologia(aelCestoPatologia);
				this.apresentarMsgNegocio( Severity.INFO,"MENSAGEM_CESTO_MATERIAL_INSERT_SUCESSO", aelCestoPatologia.getDescricao());
			}
			
			aelCestoPatologia = new AelCestoPatologia();
			
			pesquisar();
			
		} catch (BaseException e) { 
			apresentarExcecaoNegocio(e);
			aelCestoPatologia = new AelCestoPatologia();
			
		} catch(PersistenceException e) {
			LOG.error("Exceção capturada: ", e);
			if (e.getCause() instanceof ConstraintViolationException || e.getCause() instanceof NonUniqueObjectException) {
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_SIGLA_DUPLICADA",aelCestoPatologia.getSigla());
			}
			aelCestoPatologia = new AelCestoPatologia();
		}catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			if (e.getCause().getCause().getCause() instanceof ConstraintViolationException || e.getCause().getCause().getCause() instanceof NonUniqueObjectException) {
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_SIGLA_DUPLICADA",aelCestoPatologia.getSigla());
			}
			aelCestoPatologia = new AelCestoPatologia();
		}
		
	}
	
	public void ativarInativar(final AelCestoPatologia cesto) {
		try {
			String msg;
			if(DominioSituacao.A.equals(cesto.getIndSituacao())){
				cesto.setIndSituacao(DominioSituacao.I);
				msg = "MENSAGEM_CESTO_MATERIAL_INATIVADO_SUCESSO";
			} else {
				cesto.setIndSituacao(DominioSituacao.A);
				msg = "MENSAGEM_CESTO_MATERIAL_ATIVADO_SUCESSO";
			}
			
			examesPatologiaFacade.alterarAelCestoPatologia(cesto);
			this.apresentarMsgNegocio( Severity.INFO, msg, cesto.getDescricao());
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		pesquisar();
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public List<AelCestoPatologia> getLista() {
		return lista;
	}

	public void setLista(List<AelCestoPatologia> lista) {
		this.lista = lista;
	}

	public AelCestoPatologia getAelCestoPatologia() {
		return aelCestoPatologia;
	}

	public void setAelCestoPatologia(
			AelCestoPatologia aelCestoPatologia) {
		this.aelCestoPatologia = aelCestoPatologia;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public AelCestoPatologia getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelCestoPatologia selecionado) {
		this.selecionado = selecionado;
	}
}