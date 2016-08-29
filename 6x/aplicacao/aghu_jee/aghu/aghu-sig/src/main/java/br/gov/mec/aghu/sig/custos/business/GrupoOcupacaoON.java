package br.gov.mec.aghu.sig.custos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigGrupoOcupacaoCargos;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.dao.SigGrupoOcupacaoCargosDAO;
import br.gov.mec.aghu.sig.dao.SigGrupoOcupacoesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GrupoOcupacaoON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(GrupoOcupacaoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SigGrupoOcupacaoCargosDAO sigGrupoOcupacaoCargosDAO;
	
	@Inject
	private SigGrupoOcupacoesDAO sigGrupoOcupacoesDAO;
	
	private static final long serialVersionUID = -8041190985139315215L;

	public enum GrupoOcupacaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NAO_PERMITIDO_EXCLUIR_GRUPO_OCUPACAO,
		MENSAGEM_GRUPO_OCUPACAO_JA_EXISTE,
		MENSAGEM_CARGO_REPLICADO
	}
	
	public void excluirGrupoOcupacao(SigGrupoOcupacoes grupoOcupacao) throws ApplicationBusinessException{
		
		grupoOcupacao = this.getSigGrupoOcupacoesDAO().merge(grupoOcupacao);
		//Somente permite a exclusão quando não estiver vinculado a uma atividade pessoa
		if(grupoOcupacao.getListAtividadePessoas().isEmpty()){
			getSigGrupoOcupacoesDAO().remover(grupoOcupacao);
		}
		else{
			throw new ApplicationBusinessException(GrupoOcupacaoONExceptionCode.MENSAGEM_NAO_PERMITIDO_EXCLUIR_GRUPO_OCUPACAO);
		}
	}
	
	public void persistirGrupoOcupacao(SigGrupoOcupacoes grupoOcupacao)  throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//Normaliza os campos de texto
		this.normalizarCamposTexto(grupoOcupacao);
		
		//Valida os campos
		this.validarDescricaoUnicaGrupoOcupacao(grupoOcupacao);
		
		//Informa a pessoa que alterou 
		grupoOcupacao.setRapServidores(servidorLogado);
		
		//Verifica se o código já foi informado para definir se deve inserir, ou atualizar
		if(grupoOcupacao.getSeq() == null){
			//Determina a data de criação
			grupoOcupacao.setCriadoEm(new Date());
			this.getSigGrupoOcupacoesDAO().persistir(grupoOcupacao);
		}
		else{
			this.getSigGrupoOcupacoesDAO().merge(grupoOcupacao);
		}
		
		this.persistirListaGrupoOcupacaoCargos(grupoOcupacao);
	}
	
	protected void persistirListaGrupoOcupacaoCargos(SigGrupoOcupacoes grupoOcupacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//Busca os cargos vinculados de acordo como está no banco de dados
		List<SigGrupoOcupacaoCargos> listaBanco = this.getSigGrupoOcupacaoCargosDAO().obterPorGrupoOcupacao(grupoOcupacao); 
		
		//Cria váriavel local para manipular a lista que foi alterada
		List<SigGrupoOcupacaoCargos> listaAlterada = grupoOcupacao.getListGrupoOcupacaoCargos();
		
		//Para cada elemento que está no banco
		for(SigGrupoOcupacaoCargos grupoOcupacaoCargoBanco: listaBanco){
			
			//Verifica se ele ainda existe na lista atual
			SigGrupoOcupacaoCargos grupoOcupacaoCargoEncontrado = null;
			for(SigGrupoOcupacaoCargos grupoOcupacaoCargoAlterado: listaAlterada){
				if(grupoOcupacaoCargoBanco.getRapOcupacaoCargo().equals(grupoOcupacaoCargoAlterado.getRapOcupacaoCargo())){
					grupoOcupacaoCargoEncontrado = grupoOcupacaoCargoAlterado;
					break;
				}
			}
			
			//Se ele ainda existe
			if(grupoOcupacaoCargoEncontrado != null){
				
				//então deve atualizar os seus dados no banco de dados
				grupoOcupacaoCargoBanco.setRapServidores(servidorLogado);
				grupoOcupacaoCargoBanco.setIndSituacao(grupoOcupacaoCargoEncontrado.getIndSituacao());
				this.getSigGrupoOcupacaoCargosDAO().atualizar(grupoOcupacaoCargoBanco);
				
				//Remove da lista de alterações
				listaAlterada.remove(grupoOcupacaoCargoEncontrado);
			}
			//Se ele não existe mais, então tem que ser excluído do banco
			else{
				this.getSigGrupoOcupacaoCargosDAO().remover(grupoOcupacaoCargoBanco);
			}	
		}
		
		//Se ainda existe algum elemento na lista alterada, então ele deve ser inserido no banco
		if(!listaAlterada.isEmpty()){
			for(SigGrupoOcupacaoCargos grupoOcupacaoCargoAlterado: listaAlterada){
				//Complementa os campos obrigatórios
				grupoOcupacaoCargoAlterado.setSeq(null);
				grupoOcupacaoCargoAlterado.setSigGrupoOcupacoes(grupoOcupacao);
				grupoOcupacaoCargoAlterado.setCriadoEm(new Date());
				grupoOcupacaoCargoAlterado.setRapServidores(servidorLogado);
				this.getSigGrupoOcupacaoCargosDAO().persistir(grupoOcupacaoCargoAlterado);
			}
		}
	}
	
	public void validarRepeticaoCargosGrupoOcupacao(
			SigGrupoOcupacoes grupoOcupacao,
			List<SigGrupoOcupacaoCargos> listaOcupacaoCargo,
			RapOcupacaoCargo ocupacaoCargo, Integer posicao)
			throws ApplicationBusinessException {
			
		//Verifica se na lista atual já existe o cargo
		SigGrupoOcupacaoCargos ocupacaoCargoNaLista = null;
		for(int index = 0; index < listaOcupacaoCargo.size(); index++){
			
			ocupacaoCargoNaLista = listaOcupacaoCargo.get(index);
			
			if(ocupacaoCargoNaLista.getRapOcupacaoCargo().equals(ocupacaoCargo)){
				
				if(index != posicao ){
					throw new ApplicationBusinessException(GrupoOcupacaoONExceptionCode.MENSAGEM_CARGO_REPLICADO, ocupacaoCargo.getDescricao(), ocupacaoCargoNaLista.getSigGrupoOcupacoes().getDescricao());
				}
			}
		}
		
		//Busca todos os grupos que possuem o mesmo cargo
		List<SigGrupoOcupacaoCargos> listaGrupoOcupacaoCargo = this.getSigGrupoOcupacaoCargosDAO().obterPorOcupacaoCargo(ocupacaoCargo);
		
		//Para cada grupo retornado
		for(SigGrupoOcupacaoCargos grupoOcupacaoCargo : listaGrupoOcupacaoCargo){
			
			//Verifica se ambos estão no centro de custo global
			if(grupoOcupacaoCargo.getSigGrupoOcupacoes().getFccCentroCustos() == null && grupoOcupacao.getFccCentroCustos() == null){
			
				//E se não for o mesmo grupo de ocupação, dispara o erro
				if(!grupoOcupacaoCargo.getSigGrupoOcupacoes().getSeq().equals(grupoOcupacao.getSeq())){
					throw new ApplicationBusinessException(GrupoOcupacaoONExceptionCode.MENSAGEM_CARGO_REPLICADO, ocupacaoCargo.getDescricao(), grupoOcupacaoCargo.getSigGrupoOcupacoes().getDescricao());
				}
			}
			//se ambos estão em um centro de custo específico
			else if(grupoOcupacaoCargo.getSigGrupoOcupacoes().getFccCentroCustos() != null && grupoOcupacao.getFccCentroCustos() != null){
				//se for o mesmo centro de custo e outro grupo de ocupação
				if(grupoOcupacaoCargo.getSigGrupoOcupacoes().getFccCentroCustos().equals(grupoOcupacao.getFccCentroCustos()) && !grupoOcupacaoCargo.getSigGrupoOcupacoes().getSeq().equals(grupoOcupacao.getSeq())){
					throw new ApplicationBusinessException(GrupoOcupacaoONExceptionCode.MENSAGEM_CARGO_REPLICADO, ocupacaoCargo.getDescricao(), grupoOcupacaoCargo.getSigGrupoOcupacoes().getDescricao());
				}
			}
		}
	}
	
	protected void validarDescricaoUnicaGrupoOcupacao(SigGrupoOcupacoes grupoOcupacao) throws ApplicationBusinessException{
		
		//Busca registros com a mesma descrição
		List<SigGrupoOcupacoes> gruposEncontrados = this.getSigGrupoOcupacoesDAO().obterGruposPelaDescricao(grupoOcupacao.getDescricao());
		
		//Para cada registro encontrado
		for(SigGrupoOcupacoes grupoOcupacaoEncontrado : gruposEncontrados){
			if(!grupoOcupacaoEncontrado.getSeq().equals(grupoOcupacao.getSeq())){
				throw new ApplicationBusinessException(GrupoOcupacaoONExceptionCode.MENSAGEM_GRUPO_OCUPACAO_JA_EXISTE);
			}
		}
	}
	
	protected void normalizarCamposTexto(SigGrupoOcupacoes grupoOcupacao){
		//Retira os espaços em branco do campo descrição
		grupoOcupacao.setDescricao(StringUtils.trimToNull(grupoOcupacao.getDescricao()));
	}
	
	public SigGrupoOcupacoes obterGrupoOcupacaoComListaCargos(Integer seq) {
		SigGrupoOcupacoes grupoOcupacao = this.getSigGrupoOcupacoesDAO().obterPorChavePrimaria(seq, true, SigGrupoOcupacoes.Fields.CENTRO_CUSTO, SigGrupoOcupacoes.Fields.LISTA_GRUPO_OCUPACAO_CARGOS);
		for(SigGrupoOcupacaoCargos grupoOcupacaoCargo : grupoOcupacao.getListGrupoOcupacaoCargos()){
			if(grupoOcupacaoCargo.getRapOcupacaoCargo() != null){
				Hibernate.initialize(grupoOcupacaoCargo.getRapOcupacaoCargo().getDescricao());
			}
		}
		
		for(SigAtividadePessoas atividadePessoa: grupoOcupacao.getListAtividadePessoas()){
			Hibernate.initialize(atividadePessoa);
		}
		return grupoOcupacao;
	}
	
	
	protected SigGrupoOcupacoesDAO getSigGrupoOcupacoesDAO(){
		return sigGrupoOcupacoesDAO;
	}
	
	protected SigGrupoOcupacaoCargosDAO getSigGrupoOcupacaoCargosDAO(){
		return sigGrupoOcupacaoCargosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	

	
}
