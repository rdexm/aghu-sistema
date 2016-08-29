package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoMotivoAlteracaoAfDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMotivoAlteracaoAf;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Conjunto de regras de negócio para estória
 * "#5605 - Cadastro de Motivo Alteração AF"
 * 
 * @author rogeriovieira
 * 
 */
@Stateless
public class MotivoAlteracaoAfON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(MotivoAlteracaoAfON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoMotivoAlteracaoAfDAO scoMotivoAlteracaoAfDAO;

	private static final long serialVersionUID = 1310164457605268042L;

	public enum ExercicioONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_MOTIVO_ALTERACAO_JA_EXISTE
	}
	
	public Long pesquisarScoMotivoAlteracaoAfCount(ScoMotivoAlteracaoAf motivoAlteracao){
		this.normalizarCamposTexto(motivoAlteracao);
		return this.getScoMotivoAlteracaoAfDAO().pesquisarCount(motivoAlteracao);
	}
	
	public List<ScoMotivoAlteracaoAf> pesquisarScoMotivoAlteracaoAf(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, ScoMotivoAlteracaoAf motivoAlteracao){
		this.normalizarCamposTexto(motivoAlteracao);
		return this.getScoMotivoAlteracaoAfDAO().pesquisar(firstResult, maxResult, orderProperty, asc, motivoAlteracao);
	}
	
	public ScoMotivoAlteracaoAf obterScoMotivoAlteracaoAf(Short codigo) {
		return this.getScoMotivoAlteracaoAfDAO().obterPorChavePrimaria(codigo);
	}
	
	public void persistirScoMotivoAlteracaoAf(ScoMotivoAlteracaoAf motivoAlteracao) throws ApplicationBusinessException {
		
		//Normaliza os campos de texto
		this.normalizarCamposTexto(motivoAlteracao);
		
		//Valida os campos
		this.validarDescricaoUnica(motivoAlteracao);
		
		//Quando não for selecionada a situação, deve salvar por DEFAULT como Ativa
		if(motivoAlteracao.getSituacao() == null){
			motivoAlteracao.setSituacao(DominioSituacao.A);
		}
		
		//Verifica se o código já foi informado para definir se deve inserir, ou atualizar
		if(motivoAlteracao.getCodigo() == null){
			this.getScoMotivoAlteracaoAfDAO().persistir(motivoAlteracao);
		} else{
			this.getScoMotivoAlteracaoAfDAO().merge(motivoAlteracao);
		}
	}
	
	/**
	 * Este metódo verifica se já existe um registro com a mesma descrição do registro informado
	 * @param motivoAlteracao
	 * @throws ApplicationBusinessException
	 */
	private void validarDescricaoUnica(ScoMotivoAlteracaoAf motivoAlteracao) throws ApplicationBusinessException{
		//Busca um registro com a mesma descrição
		ScoMotivoAlteracaoAf motivoAlteracaoEncontrado = this.getScoMotivoAlteracaoAfDAO().obterPelaDescricao(motivoAlteracao.getDescricao());
		
		//Se foi encontrado um registro e ele possui um código diferente do registro encontrado
		if(motivoAlteracaoEncontrado != null && !motivoAlteracaoEncontrado.getCodigo().equals(motivoAlteracao.getCodigo())){
			//Dispara uma excetion com a mensagem correspondente
			throw new ApplicationBusinessException(ExercicioONExceptionCode.MENSAGEM_MOTIVO_ALTERACAO_JA_EXISTE);
		}
	}
	
	/**
	 * Este método retira os espaços em branco nos campos de texto da pesquisa e  os deixa em caixa alta
	 * @param motivoAlteracao
	 */
	private void normalizarCamposTexto(ScoMotivoAlteracaoAf motivoAlteracao){
		
		//Retira os espaços em branco do campo descrição
		motivoAlteracao.setDescricao(StringUtils.trimToNull(motivoAlteracao.getDescricao()));
		
		//Se esse campo for preenchido, então coloca ele para caixa alta
		if(motivoAlteracao.getDescricao()!= null){
			motivoAlteracao.setDescricao( motivoAlteracao.getDescricao().toUpperCase());
		}
	}
	
	public ScoMotivoAlteracaoAfDAO getScoMotivoAlteracaoAfDAO(){
		return scoMotivoAlteracaoAfDAO;
	}
}
