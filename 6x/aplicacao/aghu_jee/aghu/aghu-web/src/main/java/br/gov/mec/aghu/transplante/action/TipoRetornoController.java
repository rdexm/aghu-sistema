package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoRetorno;
import br.gov.mec.aghu.model.MtxTipoRetorno;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class TipoRetornoController extends ActionController {
	
	private static final long serialVersionUID = -5983283487265871223L;

	@EJB
	private ITransplanteFacade transplanteFacade;
	
	private MtxTipoRetorno mtxTipoRetorno = new MtxTipoRetorno();
	private List<String> listaDomTipoRetornoSelecionados = new ArrayList<String>();
	private List<String> listaDomTipoRetorno;
	private DominioSimNao dominioSimNao;
	private boolean update;
	private static final String TIPO_RETORNO_PAGINATOR = "transplante-tipoRetornoPaginator";
	private static final String MSG_TIPO_RETORNO_GRAVADO_SUCESSO = "MSG_TIPO_RETORNO_GRAVADO_SUCESSO";
	private static final String MSG_TIPO_RETORNO_ATUALIZADO_SUCESSO = "MSG_TIPO_RETORNO_ATUALIZADO_SUCESSO";
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
		carregarDominioTipoRetorno();
	}
	
	public void iniciar(){
		if(update){
			dominioSimNao = DominioSimNao.getInstance(mtxTipoRetorno.getIndSituacao().isAtivo());
			carregarDominioTipoRetornoSelecionados();
		}
	}
	
	public String gravarAtualizar(){
		String msg = MSG_TIPO_RETORNO_ATUALIZADO_SUCESSO;
		try {
			RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			if(!update){
				msg = MSG_TIPO_RETORNO_GRAVADO_SUCESSO;
				mtxTipoRetorno.setCriadoEm(new Date());
				mtxTipoRetorno.setServidor(servidor);
			}
			mtxTipoRetorno.setIndSituacao(dominioSimNao != null ? DominioSituacao.getInstance(dominioSimNao.isSim()) : null);
			mtxTipoRetorno.setIndTipo(DominioTipoRetorno.getDominioSelecionado(listaDomTipoRetornoSelecionados));
			transplanteFacade.validarTipoRetorno(mtxTipoRetorno);
			transplanteFacade.gravarAtualizarTipoRetorno(mtxTipoRetorno);
			apresentarMsgNegocio(Severity.INFO, msg);
			return voltar();
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
	}
	
	public void carregarDominioTipoRetornoSelecionados(){
		if(mtxTipoRetorno.getIndTipo().getDescricao().equals(DominioTipoRetorno.X.getDescricao())){
			listaDomTipoRetornoSelecionados.add(DominioTipoRetorno.A.getDescricao());
			listaDomTipoRetornoSelecionados.add(DominioTipoRetorno.D.getDescricao());
		}else{
			listaDomTipoRetornoSelecionados.add(mtxTipoRetorno.getIndTipo().getDescricao());
		}
	}
	
	public String voltar(){
		update = false;
		listaDomTipoRetornoSelecionados.clear();
		mtxTipoRetorno = new MtxTipoRetorno();
		dominioSimNao = null;
		return TIPO_RETORNO_PAGINATOR;
	}
	
	public void carregarDominioTipoRetorno(){
		listaDomTipoRetorno = new ArrayList<String>();
		listaDomTipoRetorno.add(DominioTipoRetorno.A.getDescricao());
		listaDomTipoRetorno.add(DominioTipoRetorno.D.getDescricao());
	}

	public MtxTipoRetorno getMtxTipoRetorno() {
		return mtxTipoRetorno;
	}

	public void setMtxTipoRetorno(MtxTipoRetorno mtxTipoRetorno) {
		this.mtxTipoRetorno = mtxTipoRetorno;
	}

	public List<String> getListaDomTipoRetornoSelecionados() {
		return listaDomTipoRetornoSelecionados;
	}

	public void setListaDomTipoRetornoSelecionados(List<String> listaDomTipoRetornoSelecionados) {
		this.listaDomTipoRetornoSelecionados = listaDomTipoRetornoSelecionados;
	}

	public List<String> getListaDomTipoRetorno() {
		return listaDomTipoRetorno;
	}

	public void setListaDomTipoRetorno(List<String> listaDomTipoRetorno) {
		this.listaDomTipoRetorno = listaDomTipoRetorno;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public DominioSimNao getDominioSimNao() {
		return dominioSimNao;
	}

	public void setDominioSimNao(DominioSimNao dominioSimNao) {
		this.dominioSimNao = dominioSimNao;
	}
}
