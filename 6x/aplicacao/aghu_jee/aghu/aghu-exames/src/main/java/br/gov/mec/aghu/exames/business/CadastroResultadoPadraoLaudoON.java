package br.gov.mec.aghu.exames.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.dao.AelDescricoesResulPadraoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoPadraoCampoDAO;
import br.gov.mec.aghu.model.AelDescricoesResulPadrao;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelResultadoPadraoCampoId;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * #5403 Cadastrar Resultado Padrão do Laudo
 * 
 * @author aghu
 * 
 */
@Stateless
public class CadastroResultadoPadraoLaudoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(CadastroResultadoPadraoLaudoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelResultadoPadraoCampoDAO aelResultadoPadraoCampoDAO;
	
	@Inject
	private AelDescricoesResulPadraoDAO aelDescricoesResulPadraoDAO;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4023525024689093747L;

	/**
	 * Procedure TFormVersaoLaudo.GravaResultPadrao
	 * 
	 * @param resultadosPadrao
	 * @param mapaParametroCamposLaudoTelaValores
	 * @throws BaseException
	 */
	public void gravarResultadoPadrao(AelVersaoLaudo versaoLaudo, AelResultadosPadrao resultadosPadrao, Map<AelParametroCamposLaudo, Object> mapaParametroCamposLaudoTelaValores)
	throws BaseException {

		// RN1: REMOVE todos os registros em AEL_RESUL_PADROES_CAMPOS da mascara selecionada
		this.removerRegistrosMascaraSelecionada(versaoLaudo, resultadosPadrao);

		// RN2: RECUPERA todos os parâmetros campos laudo do desenho
		this.recuperarParametrosCamposLaudoDesenhoMascara(versaoLaudo, resultadosPadrao, mapaParametroCamposLaudoTelaValores);

	}

	/**
	 * Remove todos os registros em AEL_RESUL_PADROES_CAMPOS da mascara selecionada
	 * 
	 * @param versaoLaudo
	 * @param resultadosPadrao
	 * @throws BaseException
	 */
	public void removerRegistrosMascaraSelecionada(AelVersaoLaudo versaoLaudo, AelResultadosPadrao resultadosPadrao) throws BaseException {

		final String emaExaSigla = versaoLaudo.getId().getEmaExaSigla();
		final Integer emaManSeq = versaoLaudo.getId().getEmaManSeq();
		final Integer seqp = versaoLaudo.getId().getSeqp();
		final Integer rpaSeq = resultadosPadrao.getSeq();
		
		// Pesquisa registros ANTERIORES de resultados padrão de campo 
		List<AelResultadoPadraoCampo> listaRegistrosRemovidos = getAelResultadoPadraoCampoDAO().pesquisarResultadoPadraoCampoPorExameMaterialResultadoPadraoSeq(emaExaSigla,
				emaManSeq, seqp, rpaSeq);
		
		for (AelResultadoPadraoCampo registroRemovido : listaRegistrosRemovidos) {
			
			// Obtém a descrição de resultado padrão 
			AelDescricoesResulPadrao descResultaPadrao = this.getAelDescricoesResulPadraoDAO().obterAelDescricoesResulPadraoPorId(registroRemovido.getId().getRpaSeq(), registroRemovido.getId().getSeqp());
			
			if(descResultaPadrao != null ){
				// Remove a descrição de resultado padrão
				this.getCadastrosApoioExamesFacade().removerAelDescricoesResulPadrao(descResultaPadrao);
			}

			// Remove o resultado padrão de campo antigo!
			this.getCadastrosApoioExamesFacade().removerAelResultadoPadraoCampo(registroRemovido);

		}
	}

	/**
	 * Recupera todos os parâmetros campos laudo do desenho
	 * 
	 * @param resultadosPadrao
	 * @param mapaParametroCamposLaudoTelaValores
	 * @throws BaseException
	 */
	public void recuperarParametrosCamposLaudoDesenhoMascara(AelVersaoLaudo versaoLaudo, AelResultadosPadrao resultadosPadrao,
			Map<AelParametroCamposLaudo, Object> mapaParametroCamposLaudoTelaValores) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final Integer rpaSeq = resultadosPadrao.getSeq();

		// Obtém o seqp máximo da tabela AEL_RESUL_PADROES_CAMPOS
		Integer seqp = this.obterMaxSeqpResultadoPadraoCampo(versaoLaudo);

		// RECUPERA todos os parâmetros campos laudo do desenho
		Set<AelParametroCamposLaudo> keys = mapaParametroCamposLaudoTelaValores.keySet();

		for (AelParametroCamposLaudo parametroCampoLaudoTela : keys) {

			seqp++; // Incrementa seqp

			// Obtém o VALOR do parâmetro campo laudo da tela
			Object valorParametroCampoLaudoTela = mapaParametroCamposLaudoTelaValores.get(parametroCampoLaudoTela);

			// Obtém o tipo de objeto visual do parâmetro campo laudo da tela
			final DominioObjetoVisual tipoObjetoVisualParametroCampoLaudoTela = parametroCampoLaudoTela.getObjetoVisual();

			if (DominioObjetoVisual.TEXTO_FIXO.equals(tipoObjetoVisualParametroCampoLaudoTela) || DominioObjetoVisual.METODO.equals(tipoObjetoVisualParametroCampoLaudoTela) || DominioObjetoVisual.EQUIPAMENTO.equals(tipoObjetoVisualParametroCampoLaudoTela)
					|| DominioObjetoVisual.RECEBIMENTO.equals(tipoObjetoVisualParametroCampoLaudoTela) || DominioObjetoVisual.HISTORICO.equals(tipoObjetoVisualParametroCampoLaudoTela)|| DominioObjetoVisual.VALORES_REFERENCIA.equals(tipoObjetoVisualParametroCampoLaudoTela)
					|| valorParametroCampoLaudoTela == null || StringUtils.isEmpty(valorParametroCampoLaudoTela.toString().trim())
					|| (parametroCampoLaudoTela.getCampoLaudo() != null && DominioTipoCampoCampoLaudo.E.equals(parametroCampoLaudoTela.getCampoLaudo().getTipoCampo()))) {
				// NÃO GRAVA o resultado padrão: Texto Fixo, Método, Equipamento, Recebimento, Resultado Anterior,Valor de Referência, CAMPOS VAZIOS ou "Númerico Expressão com campo laudo do tipo Expressão" (Adaptação do AGHU) 
				continue;
			}

			// Instancia um novo resultado padrão do campo
			AelResultadoPadraoCampo novoResultadoPadraoCampo = new AelResultadoPadraoCampo();
			
			// Instancia id do novo resultado padrão do campo
			AelResultadoPadraoCampoId id = new AelResultadoPadraoCampoId(rpaSeq, seqp);
			novoResultadoPadraoCampo.setId(id);
			
			// Seta parâmetro campo laudo, campo laudo e servidor
			novoResultadoPadraoCampo.setParametroCampoLaudo(parametroCampoLaudoTela);
			novoResultadoPadraoCampo.setServidor(servidorLogado);

			// Para o tipo Numérico/Expressão seta a coluna VALOR com o valor informado pelo usuário
			if ((DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO.equals(tipoObjetoVisualParametroCampoLaudoTela))) {
				Long valor = null;
				if (valorParametroCampoLaudoTela != null && StringUtils.isNotEmpty(valorParametroCampoLaudoTela.toString())){
					//Eliminar casas decimais
					valor = this.eliminarCasasDecimaisValorCampoLaudo(valorParametroCampoLaudoTela);
				}
				novoResultadoPadraoCampo.setValor(valor);
			}

			// Para o tipo Codificado seta RCD_GTC_SEQ e RCD_SEQP se GTCSeq for maior do que zero, caso contrário seta CAC_SEQ
			if (DominioObjetoVisual.TEXTO_CODIFICADO.equals(tipoObjetoVisualParametroCampoLaudoTela)) {

				final AelGrupoResultadoCodificado grupoResultadoCodificado = parametroCampoLaudoTela.getCampoLaudo().getGrupoResultadoCodificado();

				if (grupoResultadoCodificado != null && valorParametroCampoLaudoTela instanceof AelResultadoCodificado) {
					novoResultadoPadraoCampo.setResultadoCodificado((AelResultadoCodificado) valorParametroCampoLaudoTela);
				} else if (valorParametroCampoLaudoTela instanceof AelExameGrupoCaracteristica){
					novoResultadoPadraoCampo.setResultadoCaracteristica(((AelExameGrupoCaracteristica) valorParametroCampoLaudoTela).getResultadoCaracteristica());
				}

			}
			
			// Seta resultado padrão
			novoResultadoPadraoCampo.setResultadoPadrao(resultadosPadrao);
			
			// PERSISTE RESULTADO_PADRAO_CAMPO: Novo resultado padrão do campo
			this.getCadastrosApoioExamesFacade().inserirAelResultadoPadraoCampo(novoResultadoPadraoCampo);
			this.flush();

			// Para o tipo Alfanumérico ou Texto Longo
			if (DominioObjetoVisual.TEXTO_ALFANUMERICO.equals(tipoObjetoVisualParametroCampoLaudoTela)
					|| DominioObjetoVisual.TEXTO_LONGO.equals(tipoObjetoVisualParametroCampoLaudoTela)) {

				// Instancia uma nova descrição de resultado padrão
				AelDescricoesResulPadrao descricaoResulPadrao = new AelDescricoesResulPadrao();
				
				// Seta id da nova descrição de resultado padrão
				descricaoResulPadrao.setResultadoPadraoCampo(novoResultadoPadraoCampo);
				
				if(valorParametroCampoLaudoTela != null){
					descricaoResulPadrao.setDescricao(String.valueOf(valorParametroCampoLaudoTela));
				}
				
				// PERSISTE DESC_RESUL_PADROES_CAMPOS: Nova descrição de resultado padrão
				this.getCadastrosApoioExamesFacade().persistirAelDescricoesResulPadrao(descricaoResulPadrao);

			}


		}

	}
	
	/**
	 * Elimina as casas decimais do valor do campo laudo
	 * @param valorCampo
	 * @return
	 */
	private Long eliminarCasasDecimaisValorCampoLaudo(Object valorParametroCampoLaudoTela){
		if(valorParametroCampoLaudoTela != null){
			String valorCampo = valorParametroCampoLaudoTela.toString().replaceFirst("\\.", "").replaceAll(",", ".");
			BigDecimal b = new BigDecimal(valorCampo);
			return b.longValue();	
		}
		return null;
	}

	/**
	 * Obtém o seqp máximo da tabela AEL_RESUL_PADROES_CAMPOS
	 * 
	 * @param versaoLaudo
	 * @return
	 */
	public Integer obterMaxSeqpResultadoPadraoCampo(AelVersaoLaudo versaoLaudo) {
		return this.getAelResultadoPadraoCampoDAO().obterMaxSeqpPorExameMaterialResultado(versaoLaudo.getId().getEmaExaSigla(), versaoLaudo.getId().getEmaManSeq());
	}

	/*
	 * Facades, ONs, RNs e DAOs
	 */
	protected ICadastrosApoioExamesFacade getCadastrosApoioExamesFacade() {
		return cadastrosApoioExamesFacade;
	}

	protected AelResultadoPadraoCampoDAO getAelResultadoPadraoCampoDAO() {
		return aelResultadoPadraoCampoDAO;
	}
	
	protected AelDescricoesResulPadraoDAO getAelDescricoesResulPadraoDAO() {
		return aelDescricoesResulPadraoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
