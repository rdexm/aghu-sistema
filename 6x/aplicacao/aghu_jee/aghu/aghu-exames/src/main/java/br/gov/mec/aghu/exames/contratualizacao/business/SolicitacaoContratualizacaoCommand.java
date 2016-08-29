package br.gov.mec.aghu.exames.contratualizacao.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.contratualizacao.util.Header;
import br.gov.mec.aghu.exames.contratualizacao.util.SolicitacaoExame;
import br.gov.mec.aghu.exames.contratualizacao.vo.ItemContratualizacaoVO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


public class SolicitacaoContratualizacaoCommand extends ContratualizacaoCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6132926670394192158L;

	private static final Log LOG = LogFactory.getLog(SolicitacaoContratualizacaoCommand.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private IParametroFacade parametroFacade;

	@Inject
	private IExamesBeanFacade examesBeanFacade;

	@Inject
	private IAghuFacade aghuFacade;

	@Inject
	private IFaturamentoFacade faturamentoFacade;

	@Inject
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	public enum SolicitacaoContratualizacaoActionExceptionCode implements
	BusinessExceptionCode {
		MENSAGEM_ATENDIMENTO_NAO_LOCALIZADO, MENSAGEM_UNIDADE_FUNCIONAL_NAO_LOCALIZADA, MENSAGEM_PARAMETRO_UNIDADE_FUNCIONAL_NAO_LOCALIZADO, MENSAGEM_UNIDADE_FUNCIONAL_INATIVA, MENSAGEM_MAPA_PARAMETROS_NULO, MENSAGEM_SOLICITACAO_INTEGRACAO_NAO_ENCONTRADA, MENSAGEM_HEADER_DADOS_INTEGRACAO_NULO, MENSAGEM_LISTA_ITENS_VAZIA, 
	}

	protected static final String ATENDIMENTO_AGHU = "ATENDIMENTO_AGHU";
	protected static final String HEADER_INTEGRACAO = "HEADER_INTEGRACAO";

	private AelSolicitacaoExames solicitacao = new AelSolicitacaoExames();


	/**
	 * Executa as regras de inserção de uma solicitação de exames conforme a estória #14598
	 * {@link http://redmine084.mec.gov.br/issues/14598}
	 * @param parametros
	 * @return
	 * @throws BaseException 
	 */
	@Override
	Map<String, Object> executar(Map<String, Object> parametros) throws BaseException {
		this.executarRegrasSolicitacao(parametros);
		//		executa a inserção da solicitação e dos itens

		List<ItemContratualizacaoVO> listaItensVO = (List<ItemContratualizacaoVO>) parametros.get(ITENS_SOLICITACAO_INTEGRACAO);
		//		if (!existeItemErro(listaItensVO)) {
		String nomeMicrocomputador = (String) parametros.get(NOME_MICROCOMPUTADOR);
		listaItensVO =	getExamesBeanFacade().gravaSolicitacaoExameContratualizacao(getSolicitacao(), listaItensVO, nomeMicrocomputador);
		parametros.put(ITENS_SOLICITACAO_INTEGRACAO, listaItensVO);
		//		}
		return parametros;
	}

	//	private boolean existeItemErro(List<ItemContratualizacaoVO> listaItensVO) {
	//		boolean retorno = false;
	//		if (listaItensVO != null) {
	//			for (ItemContratualizacaoVO item : listaItensVO) {
	//				if (StringUtils.isNotEmpty(item.getMensagemErro())) {
	//					retorno = true;
	//					break;
	//				}
	//			}
	//		}
	//		return retorno;
	//	}

	protected IExamesBeanFacade getExamesBeanFacade() {
		return examesBeanFacade;
	}


	protected void associarAtendimentoSolicitacao(Map<String, Object> parametros) throws ApplicationBusinessException {
		if (parametros == null) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_MAPA_PARAMETROS_NULO);
		}
		AghAtendimentos atendimento = null;
		atendimento = (AghAtendimentos) parametros.get(ATENDIMENTO_AGHU);
		// preenche valores da solicitação
		// ATD_SEQ – AghAtendimentos
		if (atendimento == null) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_ATENDIMENTO_NAO_LOCALIZADO);
		}
		getSolicitacao().setAtendimento(atendimento);
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	/**
	 * Busca o valor do parametro P_UNIDADE_CONTRATUALIZACAO referente ao seq da classe AghUnidadesFuncionais 
	 * @return valor do tipo short 
	 * @throws ApplicationBusinessException
	 */
	protected short buscarValorNumericoParametroUnidade() throws ApplicationBusinessException {
		// UNF_SEQ – AghUnidadeFuncionais
		// Parametro de sistema – fixo zona 14 – UNF_SEQ = 33 --
		// P_UNIDADE_CONTRATUALIZACAO
		BigDecimal unfSeq = getParametroFacade().obterValorNumericoAghParametros(
				AghuParametrosEnum.P_UNIDADE_CONTRATUALIZACAO.name());
		if (unfSeq == null) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_PARAMETRO_UNIDADE_FUNCIONAL_NAO_LOCALIZADO);
		}
		return unfSeq.shortValue();
	}

	protected void associarUnidadeFuncionalSolicitacao() throws ApplicationBusinessException {
		short unfSeq = buscarValorNumericoParametroUnidade();
		AghUnidadesFuncionais unidadeFuncional = null;
		try {
			unidadeFuncional = getAghuFacade().obterUnidadeFuncional(unfSeq);
		} catch (Exception e) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_UNIDADE_FUNCIONAL_NAO_LOCALIZADA, unfSeq);
		}
		if (unidadeFuncional == null) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_UNIDADE_FUNCIONAL_NAO_LOCALIZADA, unfSeq);
		}
		// Regra 4.2.2 Valida Unidade solicitante
		if (!unidadeFuncional.isAtivo()) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_UNIDADE_FUNCIONAL_INATIVA);
		}
		getSolicitacao().setUnidadeFuncional(unidadeFuncional);
	}

	protected void associarDadosAuditoria(Map<String, Object> parametros) throws ApplicationBusinessException {
		if (parametros == null) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_MAPA_PARAMETROS_NULO);
		}
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// RECEM_NASCIDO Default do banco é N-Não
		getSolicitacao().setRecemNascido(false);
		// Regra 4.2.1 Atualiza campos de auditoria
		// CRIADO_EM Sysdate do banco de dados
		getSolicitacao().setCriadoEm(new Date());
		// Grava o servidor que está realizando a solicitação a partir do
		// usuário logado no sistema
		getSolicitacao().setServidor(servidorLogado);
	}

	protected void associarInformacoesClinicas(Map<String, Object> parametros) throws ApplicationBusinessException {
		if (parametros == null) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_MAPA_PARAMETROS_NULO);
		}
		SolicitacaoExame solicitacaoIntegracao = (SolicitacaoExame) parametros.get(ContratualizacaoCommand.SOLICITACAO_INTEGRACAO);	
		if (solicitacaoIntegracao == null) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_SOLICITACAO_INTEGRACAO_NAO_ENCONTRADA);
		}
		String informacoesClinicas = solicitacaoIntegracao.getInformacoesClinicas();
		informacoesClinicas = StringUtils.substring(informacoesClinicas, 0, 500);
		// Guardará informações que vêm no arquivo XML conforme segue:
		// Campo de Informações Clinicas (opcional) e na linha seguinte “ID
		// Prefeitura: “ + Campo IdPrefeitura
		getSolicitacao().setInformacoesClinicas(informacoesClinicas);
	}

	protected void associarConvenioSaudePlano(Map<String, Object> parametros) throws ApplicationBusinessException {
		if (parametros == null) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_MAPA_PARAMETROS_NULO);
		}
		//		Convenio SUS
		//		Plano Contratualização da Prefeitura
		Header headerIntegracao = (Header) parametros.get(HEADER_INTEGRACAO);
		if (headerIntegracao == null) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_HEADER_DADOS_INTEGRACAO_NULO);
		}
		FatConvenioSaudePlano convenioSaudePlano = this
		.getFaturamentoFacade().obterConvenioSaudePlanoAtivo((short) headerIntegracao.getConvenio(),
				headerIntegracao.getPlanoConvenio());
		getSolicitacao().setConvenioSaudePlano(convenioSaudePlano);
	}


	/**
	 * Associa os itens recebidos no map à solicitação
	 * Chama a RN de inclusão
	 * @param parametros
	 * @throws ApplicationBusinessException
	 */
	protected void associarItens(Map<String, Object> parametros) throws ApplicationBusinessException {
		if (parametros == null) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_MAPA_PARAMETROS_NULO);
		}
		List<ItemContratualizacaoVO> listaItens = (List<ItemContratualizacaoVO>) parametros.get(ITENS_SOLICITACAO_INTEGRACAO);
		if (listaItens == null || listaItens.isEmpty()) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_LISTA_ITENS_VAZIA);
		}
		//		getSolicitacao().setItensSolicitacaoExame(listaItens);
	}


	/**
	 * Gerar os exames dependentes obrigatórios para cada um dos itens da solicitação
	 * @param listaItens
	 * @throws BaseException
	 */
	protected void gerarExamesDependentes(AelSolicitacaoExames solicitacaoExames, List<ItemContratualizacaoVO> listaItensVO) throws BaseException{
		if(solicitacaoExames != null && listaItensVO != null && !listaItensVO.isEmpty()){
			for(ItemContratualizacaoVO itemVO : listaItensVO){
				if (StringUtils.isEmpty(itemVO.getMensagemErro())) { //Nao gerar dependentes se houve um erro anterior
					AelItemSolicitacaoExames itemExames = itemVO.getItemSolicitacaoExames();
					itemExames.setId(new AelItemSolicitacaoExamesId());

					SolicitacaoExameVO solicitacaoExameVO = new SolicitacaoExameVO(solicitacaoExames.getAtendimento());
					solicitacaoExameVO.setUnidadeTrabalho(solicitacaoExames.getUnidadeFuncionalAreaExecutora());
					solicitacaoExameVO.setAtendimento(solicitacaoExames.getAtendimento());

					ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO(itemExames);
					itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);

					this.getSolicitacaoExameFacade().gerarDependentes(itemSolicitacaoExameVO, itemSolicitacaoExameVO.getSolicitacaoExameVO().getUnidadeTrabalho());

					List<ItemSolicitacaoExameVO> dependentesObrigratorios = itemSolicitacaoExameVO.getDependentesObrigratorios();
					for (ItemSolicitacaoExameVO dependenteVO : dependentesObrigratorios) {
						AelItemSolicitacaoExames dependente = dependenteVO.getModel();
						itemExames.addItemSolicitacaoExame(dependente);
					}
				}
			}
		}
	}

	protected void executarRegrasSolicitacao(Map<String, Object> parametros)
	throws BaseException {
		if (parametros == null) {
			throw new ApplicationBusinessException(SolicitacaoContratualizacaoActionExceptionCode.MENSAGEM_MAPA_PARAMETROS_NULO);
		}
		if (getSolicitacao() == null) {
			setSolicitacao(new AelSolicitacaoExames());
		}
		associarAtendimentoSolicitacao(parametros);
		associarUnidadeFuncionalSolicitacao();
		associarDadosAuditoria(parametros);
		associarInformacoesClinicas(parametros);
		associarConvenioSaudePlano(parametros);
		//		Unidade preenchida quando a solicitação é realizada na área executora. Neste caso fica igual a UNF_SEQ
		getSolicitacao().setUnidadeFuncionalAreaExecutora(getSolicitacao().getUnidadeFuncional());
		//		Associar os itens à solicitação
		associarItens(parametros);
		this.gerarExamesDependentes(this.getSolicitacao(), (List<ItemContratualizacaoVO>) parametros.get(ITENS_SOLICITACAO_INTEGRACAO));
	}

	@Override
	boolean comitar() {
		// TODO Auto-generated method stub
		return false;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return solicitacaoExameFacade;
	}

	public AelSolicitacaoExames getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(AelSolicitacaoExames solicitacao) {
		this.solicitacao = solicitacao;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
