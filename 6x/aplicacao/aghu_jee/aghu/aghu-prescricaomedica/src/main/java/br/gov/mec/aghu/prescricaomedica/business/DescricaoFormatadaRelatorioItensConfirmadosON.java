package br.gov.mec.aghu.prescricaomedica.business;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacoesHemoterapicasDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoCuidadoId;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimentoId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoriaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemDispensacaoFarmaciaVO;

/**
 * 
 * 
 * Contém as regras de formatação dos itens do relatório de itens confirmados da
 * prescrição médica.
 * 
 * @author gmneto
 * 
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class DescricaoFormatadaRelatorioItensConfirmadosON extends BaseBusiness {

	private static final String _PARA_ = " PARA ";

	private static final String _DE_MAIUSCULO = " DE ";

	private static final String _DE_ = " de ";

	private static final String _DOIS_PONTOS_ = " : ";

	private static final String DILUIR_EM_ = "diluir em ";

	private static final String _ML = " ml";

	private static final String AVALIACAO_NUTRICIONISTA = "Avaliação Nutricionista";

	private static final String PARA_VAZIO = " PARA []";

	private static final String OBS = "obs.: ";

	private static final String PARA_ = "PARA ";

	private static final String DOSE = "dose";

	private static final String _H_ = " h ";

	private static final String DILUIR_EM = " Diluir em ";

	private static final String VOLUME_DILUENTE_ML = "volumeDiluenteMl";

	private static final String VELOCIDADE_DE_INFUSAO = ", velocidade de infusão ";
	
	//#54544 (Look_alike.xls): 68 medicamentos que devem utilizar a descrição que está no banco, sem utilizar upper ou lower
	private static final List<Integer> MEDICAMENTOS_DESCRICAO_NORMAL = Arrays.asList(new Integer[]{
		12750,114081,272376,195570,14885,13722,13757,13765,13927,253359,272857,13447,195553,16950,276487,14079,248681,248673,88021,16845,16640,244457,14150,
		191043,242870,270745,191060,120847,152846,14184,14192,13404,14788,14796,282141,14800,14826,14818,187020,218383,135364,18988,271374,237043,15229,
		16454,19178,276488,15210,293563,15202,16063,106496,16420,17418,271455,222976,224162,294708,282698,136158,136131,17426,155284,17523,17540,17531,17019
	});

	/**
	 * 
	 */
	private static final long serialVersionUID = 7040413244300777703L;
	
	private static final Log LOG = LogFactory.getLog(DescricaoFormatadaRelatorioItensConfirmadosON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	@EJB 
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB 
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject 
	private MpmItemPrescricaoDietaDAO mpmItemPrescricaoDietaDAO;
	
	@Inject 
	private MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO;
	
	@Inject 
	private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;
	
	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;
	
	@Inject
	private AbsSolicitacoesHemoterapicasDAO absSolicitacoesHemoterapicasDAO;
	
	@Inject
	private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;
	
	private static final String MSG_AVAL_NUTRICIONISTA = "Avaliação Nutricionista;";
	private static final String MSG_BI = "BI;";
	private static final String MSG_NUMERO_DE_VEZES_COM_VIRGULA = ", número de vezes:";
	private static final String QUEBRA_LINHA = "<br/>";
	
	private final static String LABEL_DILUIR = "Diluir";
	private final static String LABEL_ADMINISTRAR = "Administrar";
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	/**
	 * Retorna a descrição formatada da dieta para o relatório de itens
	 * confirmados.
	 * 
	 * @param dietaConfirmada
	 * @return
	 */
	public String obterDescricaoFormatadaDieta(
			MpmPrescricaoDieta dietaConfirmada, Boolean inclusaoExclusao,
			Boolean impressaoTotal) {

		final String ESPACAMENTO = " ";

		StringBuffer descricao = new StringBuffer(obterDescricaoItensDieta(dietaConfirmada,
				impressaoTotal, inclusaoExclusao));

		if (dietaConfirmada.getIndAvalNutricionista() != null
				&& dietaConfirmada.getIndAvalNutricionista().booleanValue()) {

			if (impressaoTotal) {
				descricao.append(QUEBRA_LINHA).append(MSG_AVAL_NUTRICIONISTA)
						.append(ESPACAMENTO);
			} else {
				descricao.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13))
						.append(MSG_AVAL_NUTRICIONISTA).append(ESPACAMENTO);
			}

		}

		if (dietaConfirmada.getIndBombaInfusao() != null
				&& dietaConfirmada.getIndBombaInfusao().booleanValue()) {

			if (impressaoTotal) {
				descricao.append(QUEBRA_LINHA).append(MSG_BI).append(ESPACAMENTO);
			} else {
				descricao.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13))
						.append(MSG_BI).append(ESPACAMENTO);
			}

		}

		if (dietaConfirmada.getObservacao() != null) {

			descricao.append(QUEBRA_LINHA)
					.append(CoreUtil.acrescentarEspacos(13))
					.append("obs.:")
					.append(CoreUtil.acrescentarEspacos(1))
					.append(dietaConfirmada.getObservacao().replace("\n",
							QUEBRA_LINHA + CoreUtil.acrescentarEspacos(22)));
		}

		return descricao.toString().trim();

	}

	/**
	 * Método que retorna a descrição indicando o que foi alterado em uma
	 * prescrição de dieta
	 * 
	 * @param dietaConfirmada
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public String obterDescricaoAlteracaoDieta(
			MpmPrescricaoDieta dietaConfirmada) {

		// Obtém a descrição dos itens da dieta confirmada
		String descricaoItens = obterDescricaoItensDieta(dietaConfirmada,
				false, false);

		MpmPrescricaoDieta dietaAnterior = dietaConfirmada
				.getMpmPrescricaoDietas();

		// Obtém a descrição dos itens da dieta anterior
		String descricaoItensAnterior = obterDescricaoItensDieta(dietaAnterior,
				false, false);

		boolean alterouIndAvalNutricionista = false;
		boolean alterouBombaInfusao = false;
		boolean alterouObservacao = false;

		StringBuffer descricaoAlteracao = new StringBuffer();
		StringBuffer descricaoDe = new StringBuffer(40);
		StringBuffer descricaoPara = new StringBuffer(40);

		// Verifica o que foi alterado
		if (!dietaConfirmada.getIndAvalNutricionista().equals(
				dietaAnterior.getIndAvalNutricionista())) {
			alterouIndAvalNutricionista = true;
		}
		if (!dietaConfirmada.getIndBombaInfusao().equals(
				dietaAnterior.getIndBombaInfusao())) {
			alterouBombaInfusao = true;
		}
		if ((dietaConfirmada.getObservacao() == null && dietaAnterior
				.getObservacao() != null)
				|| (dietaConfirmada.getObservacao() != null && dietaAnterior
						.getObservacao() == null)
				|| (dietaConfirmada.getObservacao() != null && !dietaConfirmada
						.getObservacao().equals(dietaAnterior.getObservacao()))) {
			alterouObservacao = true;
		}

		// Alteração de Aval de Nutricionista
		if (alterouIndAvalNutricionista) {

			if (dietaConfirmada.getIndAvalNutricionista()) {
				descricaoDe.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13))
						.append("sem Avaliação Nutricionista ");
				descricaoPara.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append(AVALIACAO_NUTRICIONISTA);
			} else{
				descricaoDe.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append(AVALIACAO_NUTRICIONISTA);
				descricaoPara.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append("sem Avaliação Nutricionista");
			}
		} else{
			if (dietaConfirmada.getIndAvalNutricionista()) {
				descricaoDe.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append(AVALIACAO_NUTRICIONISTA);
				descricaoPara.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append(AVALIACAO_NUTRICIONISTA);
			}
		}

		//Alteração de Bomba de Infusão
		if (alterouBombaInfusao) {
			if (dietaConfirmada.getIndBombaInfusao()) {
				descricaoPara.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append("BI");
			} else {
				descricaoDe.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append("BI");
			}
		} else {
			 if (dietaConfirmada.getIndBombaInfusao()){
				 descricaoDe.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append("BI");
				 descricaoPara.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append("BI");
			 }
		}

		//Alteração de Observação
		if (alterouObservacao){
			if (StringUtils.isNotBlank(dietaAnterior.getObservacao())){
				descricaoDe.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append(OBS).append(dietaAnterior.getObservacao().replace("\n", QUEBRA_LINHA + CoreUtil.acrescentarEspacos(22)));
			}

			if (StringUtils.isNotBlank(dietaConfirmada.getObservacao())){
				descricaoPara.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append(OBS).append(dietaConfirmada.getObservacao().replace("\n", QUEBRA_LINHA + CoreUtil.acrescentarEspacos(22)));
			}

		} else {
			if (StringUtils.isNotBlank(dietaConfirmada.getObservacao())){
				descricaoDe.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append(OBS).append(dietaConfirmada.getObservacao().replace("\n", QUEBRA_LINHA + CoreUtil.acrescentarEspacos(22)));
				descricaoPara.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append(OBS).append(dietaConfirmada.getObservacao().replace("\n", QUEBRA_LINHA + CoreUtil.acrescentarEspacos(22)));
			}
		}
		
		//Monta a estrutura da dieta
		descricaoAlteracao.append("DE").append(CoreUtil.acrescentarEspacos(6)).append(descricaoItensAnterior)
				.append(descricaoDe).append(QUEBRA_LINHA).append(PARA_).append(descricaoItens)
				.append(descricaoPara);
		
		return descricaoAlteracao.toString();

	}
	
	
	/**
	 * Obtém a descrição dos ítens de dieta
	 * @param dieta
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private String obterDescricaoItensDieta(MpmPrescricaoDieta dieta,
			Boolean impressaoTotal, Boolean inclusaoExclusao) {
		
		final String ESPACAMENTO = " ";
		final String FINALIZA_STRING = " ";
		
		MpmItemPrescricaoDietaDAO itemPrescricaoDietaDAO = getItemPrescricaoDietaDAO();
		
		StringBuffer descricaoItens = new StringBuffer();

		List<MpmItemPrescricaoDieta> listaItemPrescricaoDieta = itemPrescricaoDietaDAO
				.pesquisarItensDietaOrdenadoPorTipo(dieta);
		int contador = 1;
		for (MpmItemPrescricaoDieta itemPrescricaoDieta : listaItemPrescricaoDieta) {

			StringBuffer descricaoItemDieta = new StringBuffer();
			if (!impressaoTotal){
				if (!inclusaoExclusao && contador == 1){
					descricaoItemDieta.append(CoreUtil.acrescentarEspacos(2));
				} else{
					descricaoItemDieta.append(CoreUtil.acrescentarEspacos(13));
				}				
			}
			

			if (itemPrescricaoDieta.getTipoItemDieta() != null
					&& itemPrescricaoDieta.getTipoItemDieta().getDescricao() != null) {
				descricaoItemDieta.append(StringUtils
						.capitalize(itemPrescricaoDieta.getTipoItemDieta()
								.getDescricao().toLowerCase()));
			}

			if (itemPrescricaoDieta.getQuantidade() != null) {
				String quantidade = "";
				Locale locBR = new Locale("pt", "BR");//Brasil 
		        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
		        dfSymbols.setDecimalSeparator(',');
		        DecimalFormat format = new DecimalFormat("#,###,###,###,##0.##", dfSymbols);
		        quantidade = format.format(itemPrescricaoDieta.getQuantidade());
				
				descricaoItemDieta.append(ESPACAMENTO)
						.append(quantidade);
				
				if (itemPrescricaoDieta.getTipoItemDieta() != null
						&& itemPrescricaoDieta.getTipoItemDieta()
								.getUnidadeMedidaMedica() != null
						&& itemPrescricaoDieta.getTipoItemDieta()
								.getUnidadeMedidaMedica().getDescricao() != null) {
					descricaoItemDieta.append(ESPACAMENTO)
							.append(itemPrescricaoDieta.getTipoItemDieta()
									.getUnidadeMedidaMedica().getDescricao());
				}

			}

			if (itemPrescricaoDieta.getTipoFreqAprazamento() != null
					&& itemPrescricaoDieta.getTipoFreqAprazamento()
							.getSintaxe() != null
					&& itemPrescricaoDieta.getFrequencia() != null) {

				descricaoItemDieta.append(ESPACAMENTO)
						.append(itemPrescricaoDieta.getTipoFreqAprazamento()
								.getSintaxe().replaceAll(
										"#",
										itemPrescricaoDieta.getFrequencia()
												.toString()));

			} else if (itemPrescricaoDieta.getTipoFreqAprazamento() != null
					&& itemPrescricaoDieta.getTipoFreqAprazamento()
							.getDescricao() != null) {

				descricaoItemDieta.append(ESPACAMENTO)
						.append(itemPrescricaoDieta.getTipoFreqAprazamento()
								.getDescricao());

			}

			if (itemPrescricaoDieta.getNumVezes() != null) {

				descricaoItemDieta.append(MSG_NUMERO_DE_VEZES_COM_VIRGULA)
						.append(ESPACAMENTO) 
						.append(itemPrescricaoDieta.getNumVezes().toString());

			}

			descricaoItemDieta.append(FINALIZA_STRING);

			descricaoItens.append(descricaoItemDieta).append(QUEBRA_LINHA);
			contador++;

		}

		if (descricaoItens.length() >= 5){
			return descricaoItens.substring(0, descricaoItens.length() - 5); // remove o último '<br/>'			
		}
		
		return descricaoItens.toString();

	}
	
	

	/**
	 * Retorna a descrição formatada do cuidado para o relatório de itens confirmados.
	 */
	public String obterDescricaoFormatadaCuidado(final Integer atdSeq, final Long seqCuidado) {
		
		StringBuffer descricaoFormatada = new StringBuffer();
		StringBuffer descricaoTipoFreqAprazamentos = null;
		StringBuffer sintaxeCuidado = null;
		
		MpmPrescricaoCuidado cuidadoConfirmado = mpmPrescricaoCuidadoDAO.obterPorChavePrimaria(new MpmPrescricaoCuidadoId(atdSeq, seqCuidado));

		// Força carregamento de campo lazy ao acessar o getter
		MpmTipoFrequenciaAprazamento mpmTipoFreqAprazamentos = cuidadoConfirmado
				.getMpmTipoFreqAprazamentos();
		if (mpmTipoFreqAprazamentos.getSintaxe() != null
				&& cuidadoConfirmado.getFrequencia() != null) {
			descricaoTipoFreqAprazamentos = new StringBuffer(StringUtils.replace(
					mpmTipoFreqAprazamentos.getSintaxe(), "#",
					cuidadoConfirmado.getFrequencia().toString()));
		} else {
			descricaoTipoFreqAprazamentos = new StringBuffer(mpmTipoFreqAprazamentos
					.getDescricao());
		}

		if (cuidadoConfirmado.getMpmCuidadoUsuais().getIndOutros()) {
			sintaxeCuidado = new StringBuffer(StringUtils.defaultString(cuidadoConfirmado
					.getDescricao()));
		} else {
			sintaxeCuidado = new StringBuffer(cuidadoConfirmado.getMpmCuidadoUsuais()
					.getDescricao());
			if (StringUtils.isNotBlank(cuidadoConfirmado.getDescricao())) {
				sintaxeCuidado.append(" - ").append(cuidadoConfirmado.getDescricao());
			}
		}

		descricaoFormatada = new StringBuffer(sintaxeCuidado).append(", ")
				.append(descricaoTipoFreqAprazamentos);

		return StringUtils.capitalize(descricaoFormatada.toString().trim().toLowerCase());
	}
	
	
	private String obterFrequenciaFormatada(String sintaxe, Integer frequencia) {
		String returnValue = sintaxe.replaceAll("#",
				frequencia != null ? frequencia.toString() : "");
		return returnValue;
	}
	
	/**
	 * Método que retorna a descrição indicando o que foi alterado em uma
	 * prescrição de cuidado
	 *
	 * @param cuidado
	 * @return descricaoAtualizacao
	 *  
	 */
	public String obterDescricaoAlteracaoCuidado(MpmPrescricaoCuidado cuidado) {
		
		StringBuffer descricaoAtualizacao = new StringBuffer();
		
		String descricaoFrequenciaDe = "";
		String descricaoFrequenciaPara = "";
		
		MpmPrescricaoCuidado cuidadoAnterior = cuidado.getMpmPrescricaoCuidados();
		
		//Obtém a frequência anterior
		if (cuidadoAnterior.getMpmTipoFreqAprazamentos().getSintaxe() != null){
			descricaoFrequenciaDe = obterFrequenciaFormatada(cuidadoAnterior
					.getMpmTipoFreqAprazamentos().getSintaxe(), cuidadoAnterior
					.getFrequencia());
		} else {
			descricaoFrequenciaDe = cuidadoAnterior.getMpmTipoFreqAprazamentos().getDescricao();
		}
		
		//Obtém a frequência atual
		if (cuidado.getMpmTipoFreqAprazamentos().getSintaxe() != null){
			descricaoFrequenciaPara = obterFrequenciaFormatada(cuidado
					.getMpmTipoFreqAprazamentos().getSintaxe(), cuidado
					.getFrequencia());
		} else{
			descricaoFrequenciaPara = cuidado.getMpmTipoFreqAprazamentos().getDescricao();
		}

		// Verifica se alterou a descrição do cuidado usual
		if (!cuidadoAnterior.getMpmCuidadoUsuais().getDescricao().equalsIgnoreCase(cuidado.getMpmCuidadoUsuais().getDescricao())) {
			descricaoAtualizacao.append(_DE_MAIUSCULO).append(cuidadoAnterior.getMpmCuidadoUsuais().getDescricao().charAt(0)).append(cuidadoAnterior.getMpmCuidadoUsuais().getDescricao().substring(1).toLowerCase())
				.append(_PARA_).append(cuidado.getMpmCuidadoUsuais().getDescricao().charAt(0)).append(cuidado.getMpmCuidadoUsuais().getDescricao().substring(1).toLowerCase());
		} else {
			descricaoAtualizacao.append(cuidado.getMpmCuidadoUsuais().getDescricao().charAt(0)).append(cuidado.getMpmCuidadoUsuais().getDescricao().substring(1).toLowerCase());
		}

		// Verifica se alterou a descrição do cuidado
		if (StringUtils.isNotBlank(cuidadoAnterior.getDescricao()) 
				&& StringUtils.isNotBlank(cuidado.getDescricao()) 
				&& (!cuidadoAnterior.getDescricao().equals(cuidado.getDescricao()))) {
				descricaoAtualizacao.append(" - DE ").append(cuidadoAnterior.getDescricao()).append(_PARA_).append(cuidado.getDescricao()).append(", ");
		} else if (StringUtils.isNotBlank(cuidado.getDescricao())) {
				descricaoAtualizacao.append(" - ").append(cuidado.getDescricao()).append(", ");
		} else if (cuidadoAnterior.getDescricao() == null && cuidado.getDescricao() == null) {
			descricaoAtualizacao.append(", ");
		} else {
				descricaoAtualizacao.append(", ");
		}
		
		//Verifica se alterou a frequência
		if (!descricaoFrequenciaDe.equalsIgnoreCase(descricaoFrequenciaPara)){
			descricaoAtualizacao.append(_DE_MAIUSCULO).append(descricaoFrequenciaDe).append(_PARA_)
					.append(descricaoFrequenciaPara).append(',');
		} else{
			descricaoAtualizacao.append(' ').append(descricaoFrequenciaPara).append(',');
		}
		
		return descricaoAtualizacao.toString();
	}
	
	public String obterDescricaoFormatadaMedicamentoSolucaoDispensacaoFarmacia(
			MpmPrescricaoMdto medicamentoSolucao,String aprazamento)
			throws ApplicationBusinessException {

		StringBuilder strBuilder = new StringBuilder(17);

		if (medicamentoSolucao.getViaAdministracao() != null) {
			strBuilder.append(
					medicamentoSolucao.getViaAdministracao().getSigla())
					.append(", ");
		}

		// Por regra do SQL this.getTipoFreqAprazamento() nao deveria ser
		// nulo.
		if (StringUtils.isNotBlank(medicamentoSolucao.getTipoFreqAprazamento()
				.getSintaxe())) {
			strBuilder.append(medicamentoSolucao.getTipoFreqAprazamento()
					.getSintaxeFormatada(medicamentoSolucao.getFrequencia())
					.toLowerCase()+" ("+aprazamento+" )");
		} else {
			strBuilder.append(medicamentoSolucao.getTipoFreqAprazamento()
					.getDescricao()+" ("+aprazamento+" )");
		}
		strBuilder.append(", ");

		if (medicamentoSolucao.getHoraInicioAdministracao() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(DateConstants.DATE_PATTERN_HORA_MINUTO);
			strBuilder
					.append("I=")
					.append(sdf.format(medicamentoSolucao
							.getHoraInicioAdministracao())).append(" h; ");
		}
		StringBuilder stbDiluente = new StringBuilder();
		if (medicamentoSolucao.getDiluente() != null) {
			stbDiluente.append(medicamentoSolucao.getDiluente().getDescricao());
			if (medicamentoSolucao.getDiluente().getConcentracao() != null) {
				stbDiluente.append(' ').append(
						medicamentoSolucao.getDiluente()
								.getConcentracaoFormatada());
			}
			if (medicamentoSolucao.getDiluente().getMpmUnidadeMedidaMedicas() != null
					&& medicamentoSolucao.getDiluente()
							.getMpmUnidadeMedidaMedicas().getDescricao() != null) {
				stbDiluente.append(' ').append(
						medicamentoSolucao.getDiluente()
								.getMpmUnidadeMedidaMedicas().getDescricao());
			}
		}

		if (stbDiluente.length() > 0) {
			if (medicamentoSolucao.getVolumeDiluenteMl() != null) {
				strBuilder.append(DILUIR_EM)
					.append(getNumeroFormatado(medicamentoSolucao.getVolumeDiluenteMl(),VOLUME_DILUENTE_ML))
					.append(" ml de ").append(stbDiluente.toString()).append("; ");
			} else {
				strBuilder.append(DILUIR_EM).append(stbDiluente.toString())
						.append("; ");
			}
		} else {
			if (medicamentoSolucao.getVolumeDiluenteMl() != null) {
				strBuilder
						.append(DILUIR_EM)
						.append(getNumeroFormatado(
								medicamentoSolucao.getVolumeDiluenteMl(),
								VOLUME_DILUENTE_ML)).append(" ml; ");
			}
		}
		concatenarRestanteDescricaoDispensacaoFarmacia(medicamentoSolucao, strBuilder);

		return strBuilder.toString().trim();
	}
	
	
	private void concatenarRestanteDescricaoDispensacaoFarmacia(MpmPrescricaoMdto medicamentoSolucao, StringBuilder strBuilder){

		if (medicamentoSolucao.getQtdeHorasCorrer() != null) {
			strBuilder.append("Correr em ").append(
					medicamentoSolucao.getQtdeHorasCorrer());
			if (medicamentoSolucao.getUnidHorasCorrer() == null
					|| DominioUnidadeHorasMinutos.H.equals(medicamentoSolucao
							.getUnidHorasCorrer())) {
				strBuilder.append(" horas; ");
			} else {
				strBuilder.append(" minutos; ");
			}
		}

		if (medicamentoSolucao.getGotejo() != null) {
			strBuilder
					.append("Velocidade de Infusão ")
					.append(getNumeroFormatado(medicamentoSolucao.getGotejo(),
							"gotejo")).append(' ');
			if (medicamentoSolucao.getTipoVelocAdministracao() != null) {
				strBuilder.append(medicamentoSolucao
						.getTipoVelocAdministracao().getDescricao());
			} else {
				// Tipo velocidade administracao pode ser nulo, no sistema
				// antigo nao era tratado.
				strBuilder
						.append("ERRO: tipo velocidade administracao nao informado");
			}
			strBuilder.append("; ");
		}

		if (medicamentoSolucao.getIndBombaInfusao()) {
			strBuilder.append("BI").append("; ");
		}

		if (medicamentoSolucao.getIndSeNecessario()) {
			strBuilder.append("Se Necessário; ");
		}

		if (StringUtils.isNotBlank(medicamentoSolucao.getObservacao())) {
			strBuilder.append(QUEBRA_LINHA).append(
					OBS ).append( medicamentoSolucao.getObservacao());

		}

		if (medicamentoSolucao.getIndAntiMicrobiano()) {
			strBuilder.append(obterDiaDeAdministracao(medicamentoSolucao))
					.append("; ");
		}
		
	}

	/**
	 * Método que obtem a descricao do medicamento/solução para Inclusão ou Exclusão.
	 * 
	 * @param medicamentoSolucao
	 * @return
	 *  
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String obterDescricaoFormatadaMedicamentoSolucao(
			MpmPrescricaoMdto medicamentoSolucao, Boolean inclusaoExclusao,
			Boolean impressaoTotal, Boolean isUpperCase, Boolean incluirCodigoMedicamentos) throws ApplicationBusinessException {
		
		medicamentoSolucao = this.getPrescricaoMdtoDAO().merge(medicamentoSolucao);
		
		StringBuilder strBuilder = new StringBuilder(70);
		
		strBuilder.append(
				obterDescricaoDosagemItensMedicamentoSolucao(
						medicamentoSolucao, impressaoTotal, inclusaoExclusao, isUpperCase));
//				.append("; ");
		
		//É uma Solução
		if(medicamentoSolucao.getIndSolucao() != null && medicamentoSolucao.getIndSolucao()){
			if(medicamentoSolucao.getViaAdministracao()!=null){
				strBuilder.append(CoreUtil.acrescentarEspacos(13)).append(
					medicamentoSolucao.getViaAdministracao().getSigla())
					.append(", ");
			}
		}

		// Por regra do SQL this.getTipoFreqAprazamento() nao deveria ser
		// nulo.
		if (StringUtils.isNotBlank(medicamentoSolucao.getTipoFreqAprazamento()
				.getSintaxe())) {
			strBuilder.append(medicamentoSolucao.getTipoFreqAprazamento()
					.getSintaxeFormatada(medicamentoSolucao.getFrequencia()).toLowerCase());
		} else {
			strBuilder.append(medicamentoSolucao.getTipoFreqAprazamento()
					.getDescricao());
		}
		strBuilder.append(", ");

		if (medicamentoSolucao.getHoraInicioAdministracao() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(DateConstants.DATE_PATTERN_HORA_MINUTO);
			strBuilder.append("I=").append(
					sdf.format(medicamentoSolucao
							.getHoraInicioAdministracao())).append(" h; ");
		}

		StringBuilder stbDiluente = new StringBuilder();
		if (medicamentoSolucao.getDiluente() != null) {
			stbDiluente.append(medicamentoSolucao.getDiluente()
					.getDescricao());
			if (medicamentoSolucao.getDiluente().getConcentracao() != null) {
				stbDiluente.append(' ').append(
						medicamentoSolucao.getDiluente()
								.getConcentracaoFormatada());
			}
			if (medicamentoSolucao.getDiluente()
					.getMpmUnidadeMedidaMedicas() != null
					&& medicamentoSolucao.getDiluente()
							.getMpmUnidadeMedidaMedicas().getDescricao() != null) {
				stbDiluente.append(' ').append(
						medicamentoSolucao.getDiluente()
								.getMpmUnidadeMedidaMedicas()
								.getDescricao());
			}
		}

		if (stbDiluente.length() > 0) {
			if (medicamentoSolucao.getVolumeDiluenteMl() != null) {
				strBuilder.append(DILUIR_EM)
						.append(
								getNumeroFormatado(medicamentoSolucao
										.getVolumeDiluenteMl(),
										VOLUME_DILUENTE_ML)).append(
								" ml de ").append(stbDiluente.toString())
						.append("; ");
			} else {
				strBuilder.append(DILUIR_EM).append(
						stbDiluente.toString()).append("; ");
			}
		} else {
			if (medicamentoSolucao.getVolumeDiluenteMl() != null) {
				strBuilder.append(DILUIR_EM)
						.append(
								getNumeroFormatado(medicamentoSolucao
										.getVolumeDiluenteMl(),
										VOLUME_DILUENTE_ML))
						.append(" ml; ");
			}
		}

		if (medicamentoSolucao.getQtdeHorasCorrer() != null) {
			strBuilder.append("Correr em ").append(
					medicamentoSolucao.getQtdeHorasCorrer());
			if (medicamentoSolucao.getUnidHorasCorrer() == null
					|| DominioUnidadeHorasMinutos.H
							.equals(medicamentoSolucao.getUnidHorasCorrer())) {
				strBuilder.append(" horas; ");
			} else {
				strBuilder.append(" minutos; ");
			}
		}

		if (medicamentoSolucao.getGotejo() != null) {
			strBuilder.append("Velocidade de Infusão ").append(
					getNumeroFormatado(medicamentoSolucao.getGotejo(),
							"gotejo")).append(' ');
			if (medicamentoSolucao.getTipoVelocAdministracao() != null) {
				strBuilder.append(medicamentoSolucao
						.getTipoVelocAdministracao().getDescricao());
			} else {
				// Tipo velocidade administracao pode ser nulo, no sistema
				// antigo nao era tratado.
				strBuilder
						.append("ERRO: tipo velocidade administracao nao informado");
			}
			strBuilder.append("; ");
		}

		if (medicamentoSolucao.getIndBombaInfusao()) {
			strBuilder.append("BI").append("; ");
		}

		if (medicamentoSolucao.getIndSeNecessario()) {
			strBuilder.append("Se Necessário; ");
		}

		if (StringUtils.isNotBlank(medicamentoSolucao.getObservacao())) {
			strBuilder.append(QUEBRA_LINHA).append("             "
					+ OBS
					+ medicamentoSolucao.getObservacao().replace("\n",
							QUEBRA_LINHA + "                      "));

		}

		if (medicamentoSolucao.getIndAntiMicrobiano()){
			strBuilder.append(obterDiaDeAdministracao(medicamentoSolucao)).append("; ");				
		}
		
		return strBuilder.toString().trim();
	}
	
	/**
	 * Método utilizado somente para o relatório de Dispensação de Farmácia
	 * 
	 * @param medicamentoSolucao
	 * @param isUppercase
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String obterDescricaoAlteracaoMedicamentoSolucaoDispensacaoFarmacia(
			MpmPrescricaoMdto medicamentoSolucao, String aprazamento,Boolean isUppercase)
			throws ApplicationBusinessException {

		StringBuffer descricaoAtualizacao = new StringBuffer(100);
		
		Enum [] fetchArgsLeftJoin = {MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA,
				MpmPrescricaoMdto.Fields.VIA_ADMINISTRACAO, MpmPrescricaoMdto.Fields.DILUENTE, 
				MpmPrescricaoMdto.Fields.ITENS_PRESCRICAO_MDTOS, MpmPrescricaoMdto.Fields.TIPO_FREQ_APZ,MpmPrescricaoMdto.Fields.TIPO_VELC_ADM,
				MpmPrescricaoMdto.Fields.PRESCRICAO_MDTO_ORIGEM, MpmPrescricaoMdto.Fields.PRESCRICAO_MEDICA_ORIGEM};
		MpmPrescricaoMdto medicamentoSolucaoAnterior = getPrescricaoMdtoDAO().obterPorChavePrimaria(
				new MpmPrescricaoMdtoId(medicamentoSolucao.getPrescricaoMdtoOrigem().getId().getAtdSeq(), medicamentoSolucao.getPrescricaoMdtoOrigem().getId().getSeq()),
				null, fetchArgsLeftJoin);

		/*----- Obtém valores equivalentes aos campos da view -----*/

		// Frequencia
		final String freqEditDe = obterFrequenciaFormatadaMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String freqEditPara = obterFrequenciaFormatadaMedicamentoSolucao(medicamentoSolucao);

		// Volume Diluente
		final String volumeDiluenteMlDe = obterVolumeDiluenteMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String volumeDiluenteMlPara = obterVolumeDiluenteMedicamentoSolucao(medicamentoSolucao);

		// Descrição Diluente
		final String diluenteEditDe = obterDescricaoDiluenteMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String diluenteEditPara = obterDescricaoDiluenteMedicamentoSolucao(medicamentoSolucao);

		// Quantidade Horas Correr
		final String qtdeCorrerDe = obterQtdeHorasCorrerMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String qtdeCorrerPara = obterQtdeHorasCorrerMedicamentoSolucao(medicamentoSolucao);

		// Unidade Horas Correr
		final String unidHorasCorrerDe = obterUnidadeHorasCorrerMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String unidHorasCorrerPara = obterUnidadeHorasCorrerMedicamentoSolucao(medicamentoSolucao);

		// Gotejo
		final String gotejoDe = obterGotejoMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String gotejoPara = obterGotejoMedicamentoSolucao(medicamentoSolucao);

		// Tipo de Velocidade
		final String descricaoTvaDe = obterTipoVelocAdministracaoMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String descricaoTvaPara = obterTipoVelocAdministracaoMedicamentoSolucao(medicamentoSolucao);
		/* ------------------------------------------------------- */

		//Via de Administração e Hora
		concatenarViaHoraAdministracaoAlteracaoDispFarmacia(
				descricaoAtualizacao, medicamentoSolucao,
				medicamentoSolucaoAnterior, freqEditDe, freqEditPara, aprazamento);
		// Volume do Diluente
		concatenarVolumeDiluenteAlteracaoDispFarmacia(descricaoAtualizacao,
				diluenteEditDe, diluenteEditPara, volumeDiluenteMlDe,
				volumeDiluenteMlPara);
		// Horas a correr
		concatenarHorasCorrerAlteracaoDispFarmacia(descricaoAtualizacao,
				qtdeCorrerDe, qtdeCorrerPara, unidHorasCorrerDe,
				unidHorasCorrerPara);
		// Gotejo
		concatenarGotejoAlteracaoDispFarmacia(descricaoAtualizacao, gotejoDe,
				gotejoPara, descricaoTvaDe, descricaoTvaPara);
		

		// Bomba de Infusão
		if (!medicamentoSolucaoAnterior.getIndBombaInfusao().equals(
				medicamentoSolucao.getIndBombaInfusao())) {
			if (medicamentoSolucaoAnterior.getIndBombaInfusao()) {
				descricaoAtualizacao.append(", DE com BI");
			} else {
				descricaoAtualizacao.append(", DE sem BI");
			}
			if (medicamentoSolucao.getIndBombaInfusao()) {
				descricaoAtualizacao.append(" PARA com BI");
			} else {
				descricaoAtualizacao.append(" PARA sem BI");
			}
		} else {
			if (medicamentoSolucao.getIndBombaInfusao()) {
				descricaoAtualizacao.append(", BI");
			}
		}

		// Ind seNecessário
		if (!medicamentoSolucaoAnterior.getIndSeNecessario().equals(
				medicamentoSolucao.getIndSeNecessario())) {
			if (medicamentoSolucaoAnterior.getIndSeNecessario()) {
				descricaoAtualizacao.append(", DE se necessário");
			} else {
				descricaoAtualizacao.append(", DE sem se necessário");
			}
			if (medicamentoSolucao.getIndSeNecessario()) {
				descricaoAtualizacao.append(" PARA se necessário");
			} else {
				descricaoAtualizacao.append(" PARA sem se necessário");
			}
		} else {
			if (medicamentoSolucao.getIndSeNecessario()) {
				descricaoAtualizacao.append(", se necessário");
			}
		}

		// Observação

		/*--TODO: ESTE TRECHO SÓ FOI COLOCADO AQUI PORQUE UM VALOR FORMATADO ESTÁ SENDO SETADO NO ATRIBUTO
		 * OBSERVAÇÃO DURANTE O RELATÓRIO DE FARMÁCIAS (QUE EXECUTA ANTES DESTE). UM REFACTORING DEVE SER PROVIDENCIADO
		 * PARA O RELATÓRIO DE FARMÁCIAS */
		MpmPrescricaoMdtoDAO prescricaoMdtoDAO = getPrescricaoMdtoDAO();
		String observacaoBanco = prescricaoMdtoDAO
				.obterObservacaoDoBanco(medicamentoSolucao.getId());
		medicamentoSolucao.setObservacao(observacaoBanco);
		/*---------------------------------------------------------------------------------------------*/

		if ((medicamentoSolucaoAnterior.getObservacao() != null && medicamentoSolucao
				.getObservacao() == null)
				|| (medicamentoSolucaoAnterior.getObservacao() == null && medicamentoSolucao
						.getObservacao() != null)
				|| (medicamentoSolucaoAnterior.getObservacao() != null && !medicamentoSolucaoAnterior
						.getObservacao().equals(
								medicamentoSolucao.getObservacao()))) {

			if (medicamentoSolucao.getObservacao() != null
					&& medicamentoSolucaoAnterior.getObservacao() != null) {

				descricaoAtualizacao
						.append(',')
						.append(QUEBRA_LINHA)
						.append(CoreUtil.acrescentarEspacos(13))
						.append("obs.: DE ")
						.append(medicamentoSolucaoAnterior
								.getObservacao()
								.replace(
										"\n",
										QUEBRA_LINHA
												+ CoreUtil
														.acrescentarEspacos(28)));

				descricaoAtualizacao
						.append(',')
						.append(QUEBRA_LINHA)
						.append(CoreUtil.acrescentarEspacos(13))
						.append(CoreUtil.acrescentarEspacos(9))
						.append(PARA_)
						.append(medicamentoSolucao
								.getObservacao()
								.replace(
										"\n",
										QUEBRA_LINHA
												+ CoreUtil
														.acrescentarEspacos(33)));
			} else {
				if (medicamentoSolucao.getObservacao() != null) {
					descricaoAtualizacao
							.append(',')
							.append(QUEBRA_LINHA)
							.append(CoreUtil.acrescentarEspacos(13))
							.append(OBS)
							.append(medicamentoSolucao.getObservacao().replace(
									"\n",
									QUEBRA_LINHA + CoreUtil.acrescentarEspacos(22)));
				}
			}
		} else {
			if (medicamentoSolucao.getObservacao() != null) {
				descricaoAtualizacao
						.append(',')
						.append(QUEBRA_LINHA)
						.append(CoreUtil.acrescentarEspacos(13))
						.append(OBS)
						.append(medicamentoSolucao
								.getObservacao()
								.replace(
										"\n",
										QUEBRA_LINHA
												+ CoreUtil
														.acrescentarEspacos(22)));
			}
		}

		if (medicamentoSolucao.getIndAntiMicrobiano()) {
			descricaoAtualizacao.append(", ")
					.append(obterDiaDeAdministracao(medicamentoSolucao))
					.append("; ");
		}

		return descricaoAtualizacao.toString();
	}
	
	private void concatenarGotejoAlteracaoDispFarmacia(StringBuffer descricaoAtualizacao,
			String gotejoDe, String gotejoPara, String descricaoTvaDe,
			String descricaoTvaPara) {
		// Gotejo
		if (gotejoPara != null || gotejoDe != null) {
			if ((gotejoPara != null && gotejoPara == null)
					|| (gotejoPara == null && gotejoPara != null)
					|| (gotejoPara != null && !gotejoPara.equals(gotejoDe))) {

				if (gotejoDe != null && gotejoPara != null) {
					descricaoAtualizacao.append(", DE velocidade de infusão ")
							.append(gotejoDe).append(' ')
							.append(descricaoTvaDe)
							.append(" PARA velocidade de infusão ")
							.append(gotejoPara).append(' ')
							.append(descricaoTvaPara);
				} else {
					if (gotejoPara != null) {
						descricaoAtualizacao.append(VELOCIDADE_DE_INFUSAO)
								.append(gotejoPara).append(' ')
								.append(descricaoTvaPara);
					}
				}
			} else {
				if (gotejoPara != null) {
					descricaoAtualizacao.append(VELOCIDADE_DE_INFUSAO)
							.append(gotejoPara).append(' ')
							.append(descricaoTvaPara);
				}
			}
		}
		
	}

	private void concatenarHorasCorrerAlteracaoDispFarmacia(StringBuffer descricaoAtualizacao,
			String qtdeCorrerDe, String qtdeCorrerPara,
			String unidHorasCorrerDe, String unidHorasCorrerPara) {

		// Horas Correr
		if (qtdeCorrerPara != null || unidHorasCorrerPara != null
				|| qtdeCorrerDe != null || unidHorasCorrerDe != null) {
			if ((qtdeCorrerDe != null && qtdeCorrerPara == null)
					|| (qtdeCorrerDe == null && qtdeCorrerPara != null)
					|| (unidHorasCorrerDe != null && unidHorasCorrerPara == null)
					|| (unidHorasCorrerDe == null && unidHorasCorrerPara != null)
					|| ((qtdeCorrerDe != null && !qtdeCorrerDe
							.equals(qtdeCorrerPara)) || (unidHorasCorrerDe != null && !unidHorasCorrerDe
							.equals(unidHorasCorrerPara)))) {
	
				if (qtdeCorrerDe != null) {
					descricaoAtualizacao.append(", DE correr em ")
							.append(qtdeCorrerDe).append(unidHorasCorrerDe);
				} else {
					descricaoAtualizacao.append(" , DE []");
				}
	
				if (qtdeCorrerPara != null) {
					descricaoAtualizacao.append(" PARA correr em ")
							.append(qtdeCorrerPara).append(unidHorasCorrerPara);
				} else {
					descricaoAtualizacao.append(PARA_VAZIO);
				}
			} else {
				if (qtdeCorrerPara != null) {
					descricaoAtualizacao.append(", correr em ")
							.append(qtdeCorrerPara).append(unidHorasCorrerPara);
				}
			}
		}
		
	}

	private void concatenarVolumeDiluenteAlteracaoDispFarmacia(
			StringBuffer descricaoAtualizacao, String diluenteEditDe,
			String diluenteEditPara, String volumeDiluenteMlDe,
			String volumeDiluenteMlPara) {
			// Volume Diluente
			String vDiluenteDe = "";
			String vDiluentePara = "";
			if (StringUtils.isNotBlank(volumeDiluenteMlPara)
					|| diluenteEditPara != null
					|| StringUtils.isNotBlank(volumeDiluenteMlDe)
					|| diluenteEditDe != null) {

				if (StringUtils.isNotBlank(volumeDiluenteMlDe)
						|| diluenteEditDe != null) {
					vDiluenteDe = "";
					if (StringUtils.isNotBlank(volumeDiluenteMlDe)
							&& diluenteEditDe != null) {
						vDiluenteDe = DILUIR_EM_ + volumeDiluenteMlDe + _ML
								+ _DE_ + diluenteEditDe;
					} else if (StringUtils.isNotBlank(volumeDiluenteMlDe)
							&& diluenteEditDe == null) {
						vDiluenteDe = DILUIR_EM_ + volumeDiluenteMlDe + _ML;
					} else if (StringUtils.isBlank(volumeDiluenteMlDe)
							&& diluenteEditDe != null) {
						vDiluenteDe = DILUIR_EM_ + diluenteEditDe;
					}
				} else {
					vDiluenteDe = "[]";
				}

				if (StringUtils.isNotBlank(volumeDiluenteMlPara)
						|| diluenteEditPara != null) {
					vDiluentePara = "";
					if (StringUtils.isNotBlank(volumeDiluenteMlPara)
							&& diluenteEditPara != null) {
						vDiluentePara = DILUIR_EM_ + volumeDiluenteMlPara + _ML
								+ _DE_ + diluenteEditPara;
					} else if (StringUtils.isNotBlank(volumeDiluenteMlPara)
							&& diluenteEditPara == null) {
						vDiluentePara = DILUIR_EM_ + volumeDiluenteMlPara + _ML;
					} else if (StringUtils.isBlank(volumeDiluenteMlPara)
							&& diluenteEditPara != null) {
						vDiluentePara = DILUIR_EM_ + diluenteEditPara;
					}
				} else {
					vDiluentePara = "[]";
				}

				if (!vDiluenteDe.equalsIgnoreCase(vDiluentePara)) {
					descricaoAtualizacao.append(", DE ").append(vDiluenteDe)
							.append(_PARA_).append(vDiluentePara);
				} else {
					descricaoAtualizacao.append(", ").append(vDiluentePara);
				}
			}

	}

		private void concatenarViaHoraAdministracaoAlteracaoDispFarmacia(
			StringBuffer descricaoAtualizacao,
			MpmPrescricaoMdto medicamentoSolucao, MpmPrescricaoMdto medicamentoSolucaoAnterior, String freqEditDe, String freqEditPara, String aprazamento) {
			// Via Administração
			if (!medicamentoSolucao.getViaAdministracao().equals(
					medicamentoSolucaoAnterior.getViaAdministracao())) {
				descricaoAtualizacao
						.append(_DE_MAIUSCULO)
						.append(medicamentoSolucaoAnterior.getViaAdministracao()
								.getSigla())
						.append(_PARA_)
						.append(medicamentoSolucao.getViaAdministracao().getSigla())
						.append(", ");
			} else {
				descricaoAtualizacao
						.append(' ')
						.append(medicamentoSolucao.getViaAdministracao().getSigla())
						.append(", ");
			}

			// Frequencia
			if (!freqEditDe.equals(freqEditPara)) {
				descricaoAtualizacao.append("DE ").append(freqEditDe)
						.append(_PARA_).append(freqEditPara+"("+aprazamento+")");
			} else {
				descricaoAtualizacao.append(freqEditPara+"("+aprazamento+")");
			}

			// Hora Início de Administração
			SimpleDateFormat sdf = new SimpleDateFormat(DateConstants.DATE_PATTERN_HORA_MINUTO);
			if (medicamentoSolucao.getHoraInicioAdministracao() != null
					|| medicamentoSolucaoAnterior.getHoraInicioAdministracao() != null) {

				if ((medicamentoSolucao.getHoraInicioAdministracao() != null && medicamentoSolucaoAnterior
						.getHoraInicioAdministracao() == null)
						|| (medicamentoSolucao.getHoraInicioAdministracao() == null && medicamentoSolucaoAnterior
								.getHoraInicioAdministracao() != null)
						|| (medicamentoSolucao.getHoraInicioAdministracao() != null
								&& medicamentoSolucaoAnterior
										.getHoraInicioAdministracao() != null && medicamentoSolucao
								.getHoraInicioAdministracao().compareTo(
										medicamentoSolucaoAnterior
												.getHoraInicioAdministracao()) != 0)) {

					if (medicamentoSolucaoAnterior.getHoraInicioAdministracao() != null) {
						descricaoAtualizacao
								.append(", DE I= ")
								.append(sdf.format(medicamentoSolucaoAnterior
										.getHoraInicioAdministracao()))
								.append(_H_);
					} else {
						descricaoAtualizacao.append(", DE I= []");
					}

					if (medicamentoSolucao.getHoraInicioAdministracao() != null) {
						descricaoAtualizacao
								.append(" PARA I= ")
								.append(sdf.format(medicamentoSolucao
										.getHoraInicioAdministracao()))
								.append(_H_);
					} else {
						descricaoAtualizacao.append(PARA_VAZIO);
					}
				} else {
					descricaoAtualizacao.append(", I= ").append(
							sdf.format(medicamentoSolucao
									.getHoraInicioAdministracao()));
				}
			}
		
	}

		/**
	 * Método que obtem a descricao do medicamento/solução para Alteração.
	 * @param medicamentoSolucao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public String obterDescricaoAlteracaoMedicamentoSolucao(
			MpmPrescricaoMdto medicamentoSolucao, Boolean isUppercase) throws ApplicationBusinessException {

		StringBuffer descricaoAtualizacao = new StringBuffer(35);
		//MpmPrescricaoMdto medicamentoSolucaoAnterior = medicamentoSolucao.getPrescricaoMdtoOrigem();
		Enum [] fetchArgsLeftJoin = {MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA,
				MpmPrescricaoMdto.Fields.VIA_ADMINISTRACAO, MpmPrescricaoMdto.Fields.DILUENTE, 
				MpmPrescricaoMdto.Fields.ITENS_PRESCRICAO_MDTOS, MpmPrescricaoMdto.Fields.TIPO_FREQ_APZ,MpmPrescricaoMdto.Fields.TIPO_VELC_ADM,
				MpmPrescricaoMdto.Fields.PRESCRICAO_MDTO_ORIGEM, MpmPrescricaoMdto.Fields.PRESCRICAO_MEDICA_ORIGEM};
		MpmPrescricaoMdto medicamentoSolucaoAnterior = getPrescricaoMdtoDAO().obterPorChavePrimaria(
				new MpmPrescricaoMdtoId(medicamentoSolucao.getPrescricaoMdtoOrigem().getId().getAtdSeq(), medicamentoSolucao.getPrescricaoMdtoOrigem().getId().getSeq()),
				null, fetchArgsLeftJoin);

		/*----- Obtém valores equivalentes aos campos da view -----*/
		//Descrição Formatada
		final String medDescricaoEditDe = obterDescricaoDosagemItensMedicamentoSolucao(medicamentoSolucaoAnterior, false, false, isUppercase);
		final String medDescricaoEditPara = obterDescricaoDosagemItensMedicamentoSolucao(medicamentoSolucao, false, false, isUppercase);
		
		final String medDescricaoAlteradaEditPara = obterDescricaoAlteradaDosagemItensMedicamentoSolucao(medicamentoSolucao, false, false, isUppercase);
		
		// Descrição Sem Dosagem
		final String medDescricaoSemDosagemEdit = obterDescricaoSemDosagemItensMedicamentoSolucao(medicamentoSolucao, false, false, isUppercase);

		// Descrição Dosagem
		final String medAdministracaoDosagemEditDe = obterAdministracaoDosagemItensMedicamentoSolucao(medicamentoSolucaoAnterior, false, false);
		final String medAdministracaoDosagemEditPara = obterAdministracaoDosagemItensMedicamentoSolucao(medicamentoSolucao, false, false);

		//Frequencia
		final String freqEditDe = obterFrequenciaFormatadaMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String freqEditPara = obterFrequenciaFormatadaMedicamentoSolucao(medicamentoSolucao);
		
		// Volume Diluente
		final String volumeDiluenteMlDe = obterVolumeDiluenteMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String volumeDiluenteMlPara = obterVolumeDiluenteMedicamentoSolucao(medicamentoSolucao);
		
		// Descrição Diluente
		final String diluenteEditDe = obterDescricaoDiluenteMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String diluenteEditPara = obterDescricaoDiluenteMedicamentoSolucao(medicamentoSolucao);
		
		// Quantidade Horas Correr
		final String qtdeCorrerDe = obterQtdeHorasCorrerMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String qtdeCorrerPara = obterQtdeHorasCorrerMedicamentoSolucao(medicamentoSolucao);
		
		// Unidade Horas Correr
		final String unidHorasCorrerDe = obterUnidadeHorasCorrerMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String unidHorasCorrerPara = obterUnidadeHorasCorrerMedicamentoSolucao(medicamentoSolucao);
		
		// Gotejo
		final String gotejoDe = obterGotejoMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String gotejoPara = obterGotejoMedicamentoSolucao(medicamentoSolucao);
		
		// Tipo de Velocidade
		final String descricaoTvaDe = obterTipoVelocAdministracaoMedicamentoSolucao(medicamentoSolucaoAnterior);
		final String descricaoTvaPara = obterTipoVelocAdministracaoMedicamentoSolucao(medicamentoSolucao);
		/* ------------------------------------------------------- */

		// Solução
		if (medicamentoSolucao.getIndSolucao() != null && medicamentoSolucao.getIndSolucao()) {
			descricaoAtualizacao.append("DE").append(CoreUtil.acrescentarEspacos(8))
					.append(StringUtils.capitalize(medDescricaoEditDe)).append(" PARA").append(CoreUtil.acrescentarEspacos(3))
					.append(StringUtils.capitalize(medDescricaoEditPara));
		// Medicamento
		} else if (!medAdministracaoDosagemEditDe.equals(medAdministracaoDosagemEditPara)) { 
			descricaoAtualizacao.append(medDescricaoSemDosagemEdit);
			descricaoAtualizacao.append(" - DE ")
					.append(StringUtils.capitalize(medAdministracaoDosagemEditDe))
					.append(" - PARA ")
					.append(StringUtils.capitalize(medAdministracaoDosagemEditPara)).append(',');
		// Inclusão e Exclusão de MEDICAMENTO.
		} else { 
			descricaoAtualizacao.append(medDescricaoAlteradaEditPara);
		}

		// Via Administração
		if (!medicamentoSolucao.getViaAdministracao().equals(
				medicamentoSolucaoAnterior.getViaAdministracao())) {
			descricaoAtualizacao.append(_DE_MAIUSCULO)
					.append(medicamentoSolucaoAnterior.getViaAdministracao()
							.getSigla()).append(_PARA_)
					.append(medicamentoSolucao.getViaAdministracao().getSigla())
					.append(", ");
		} else {
			descricaoAtualizacao.append(' ').append(medicamentoSolucao.getViaAdministracao()
					.getSigla())
					.append(", ");
		}
		
		//Frequencia
		if (!freqEditDe.equals(freqEditPara)){
			descricaoAtualizacao.append("DE ").append(freqEditDe).append(_PARA_).append(freqEditPara);
		} else{
			descricaoAtualizacao.append(freqEditPara);
		}
		
		//Hora Início de Administração
		SimpleDateFormat sdf = new SimpleDateFormat(DateConstants.DATE_PATTERN_HORA_MINUTO);
		if (medicamentoSolucao.getHoraInicioAdministracao() != null
				|| medicamentoSolucaoAnterior.getHoraInicioAdministracao() != null) {
			
			if ((medicamentoSolucao.getHoraInicioAdministracao() != null && medicamentoSolucaoAnterior
					.getHoraInicioAdministracao() == null)
					|| (medicamentoSolucao.getHoraInicioAdministracao() == null && medicamentoSolucaoAnterior
							.getHoraInicioAdministracao() != null)
					|| (medicamentoSolucao.getHoraInicioAdministracao() != null
							&& medicamentoSolucaoAnterior.getHoraInicioAdministracao() != null && medicamentoSolucao
							.getHoraInicioAdministracao().compareTo(
									medicamentoSolucaoAnterior.getHoraInicioAdministracao()) != 0)) {
			
				if (medicamentoSolucaoAnterior.getHoraInicioAdministracao() != null){
					descricaoAtualizacao.append(", DE I= ").append(sdf.format(medicamentoSolucaoAnterior.getHoraInicioAdministracao())).append(_H_);
				} else{
					descricaoAtualizacao.append(", DE I= []");
				}
				
				if (medicamentoSolucao.getHoraInicioAdministracao() != null){
					descricaoAtualizacao.append(" PARA I= ").append(sdf.format(medicamentoSolucao.getHoraInicioAdministracao())).append(_H_);
				} else{
					descricaoAtualizacao.append(PARA_VAZIO);
				}
			} else{
				descricaoAtualizacao.append(", I= ").append(sdf.format(medicamentoSolucao.getHoraInicioAdministracao()));
			}
		}
		
		//Volume Diluente
		String vDiluenteDe = "";
		String vDiluentePara = "";
		if (StringUtils.isNotBlank(volumeDiluenteMlPara)
				|| diluenteEditPara != null
				|| StringUtils.isNotBlank(volumeDiluenteMlDe)
				|| diluenteEditDe != null) {
			
			if (StringUtils.isNotBlank(volumeDiluenteMlDe) || diluenteEditDe != null){
				vDiluenteDe = "";
				if (StringUtils.isNotBlank(volumeDiluenteMlDe) && diluenteEditDe != null){
					vDiluenteDe = DILUIR_EM_ + volumeDiluenteMlDe + _ML + _DE_ + diluenteEditDe;
				}
				else if (StringUtils.isNotBlank(volumeDiluenteMlDe) && diluenteEditDe == null){
					vDiluenteDe = DILUIR_EM_ + volumeDiluenteMlDe + _ML;
				}
				else if (StringUtils.isBlank(volumeDiluenteMlDe) && diluenteEditDe != null){
					vDiluenteDe = DILUIR_EM_ + diluenteEditDe;
				}
			}
			else{
				vDiluenteDe = "[]";
			}
			
			if (StringUtils.isNotBlank(volumeDiluenteMlPara) || diluenteEditPara != null){
				vDiluentePara = "";
				if (StringUtils.isNotBlank(volumeDiluenteMlPara) && diluenteEditPara != null){
					vDiluentePara = DILUIR_EM_ + volumeDiluenteMlPara + _ML + _DE_ + diluenteEditPara;
				}
				else if (StringUtils.isNotBlank(volumeDiluenteMlPara) && diluenteEditPara == null){
					vDiluentePara = DILUIR_EM_ + volumeDiluenteMlPara + _ML;
				}
				else if (StringUtils.isBlank(volumeDiluenteMlPara) && diluenteEditPara != null){
					vDiluentePara = DILUIR_EM_ + diluenteEditPara;
				}
			}
			else{
				vDiluentePara = "[]";
			}
			
			if (!vDiluenteDe.equalsIgnoreCase(vDiluentePara)){
				descricaoAtualizacao.append(", DE ").append(vDiluenteDe).append(_PARA_).append(vDiluentePara);
			}
			else{
				descricaoAtualizacao.append(", ").append(vDiluentePara);
			}
		}
		
		
		//Horas Correr
		if (qtdeCorrerPara != null || unidHorasCorrerPara != null || qtdeCorrerDe != null || unidHorasCorrerDe != null){
			if ((qtdeCorrerDe != null && qtdeCorrerPara == null)
					|| (qtdeCorrerDe == null && qtdeCorrerPara != null)
					|| (unidHorasCorrerDe != null && unidHorasCorrerPara == null)
					|| (unidHorasCorrerDe == null && unidHorasCorrerPara != null)
					|| ((qtdeCorrerDe != null && !qtdeCorrerDe
							.equals(qtdeCorrerPara)) || (unidHorasCorrerDe != null && !unidHorasCorrerDe
							.equals(unidHorasCorrerPara)))) {
				
				if (qtdeCorrerDe != null){
					descricaoAtualizacao.append(", DE correr em ").append(qtdeCorrerDe).append(unidHorasCorrerDe);
				}
				else{
					descricaoAtualizacao.append(" , DE []");
				}
				
				if (qtdeCorrerPara != null){
					descricaoAtualizacao.append(" PARA correr em ").append(qtdeCorrerPara).append(unidHorasCorrerPara);
				}
				else{
					descricaoAtualizacao.append(PARA_VAZIO);
				}
			}
			else{
				if (qtdeCorrerPara != null){
					descricaoAtualizacao.append(", correr em ").append(qtdeCorrerPara).append(unidHorasCorrerPara);
				}
			}
		}
		
		// Gotejo
		if (gotejoPara != null || gotejoDe != null) {
			if ((gotejoPara != null && gotejoPara == null)
					|| (gotejoPara == null && gotejoPara != null)
					|| (gotejoPara != null && !gotejoPara.equals(gotejoDe))) {

				if (gotejoDe != null && gotejoPara != null) {
					descricaoAtualizacao.append(", DE velocidade de infusão ").append(gotejoDe).append(' ')
							.append(descricaoTvaDe).append(" PARA velocidade de infusão ").append(gotejoPara)
							.append(' ').append(descricaoTvaPara);
				} else {
					if (gotejoPara != null) {
						descricaoAtualizacao.append(VELOCIDADE_DE_INFUSAO).append(gotejoPara).append(' ')
								.append(descricaoTvaPara);
					}
				}
			} else {
				if (gotejoPara != null) {
					descricaoAtualizacao.append(VELOCIDADE_DE_INFUSAO).append(gotejoPara).append(' ')
							.append(descricaoTvaPara);
				}
			}
		}
		
		//Bomba de Infusão
		if (!medicamentoSolucaoAnterior.getIndBombaInfusao().equals(medicamentoSolucao.getIndBombaInfusao())) {
			if (medicamentoSolucaoAnterior.getIndBombaInfusao()){
				descricaoAtualizacao.append(", DE com BI");
			} else{
				descricaoAtualizacao.append(", DE sem BI");
			}
			if (medicamentoSolucao.getIndBombaInfusao()){
				descricaoAtualizacao.append(" PARA com BI");
			} else{
				descricaoAtualizacao.append(" PARA sem BI");
			}
		} else{
			if (medicamentoSolucao.getIndBombaInfusao()){
				descricaoAtualizacao.append(", BI");
			}
		}
		
		//Ind seNecessário
		if (!medicamentoSolucaoAnterior.getIndSeNecessario().equals(medicamentoSolucao.getIndSeNecessario())){
			if (medicamentoSolucaoAnterior.getIndSeNecessario()){
				descricaoAtualizacao.append(", DE se necessário");
			} else{
				descricaoAtualizacao.append(", DE sem se necessário");
			}
			if (medicamentoSolucao.getIndSeNecessario()){
				descricaoAtualizacao.append(" PARA se necessário");
			} else{
				descricaoAtualizacao.append(" PARA sem se necessário");
			}
		} else{
			if (medicamentoSolucao.getIndSeNecessario()){
				descricaoAtualizacao.append(", se necessário");
			}
		}
		
		
		//Observação
		
		/*--TODO: ESTE TRECHO SÓ FOI COLOCADO AQUI PORQUE UM VALOR FORMATADO ESTÁ SENDO SETADO NO ATRIBUTO
		 * OBSERVAÇÃO DURANTE O RELATÓRIO DE FARMÁCIAS (QUE EXECUTA ANTES DESTE). UM REFACTORING DEVE SER PROVIDENCIADO
		 * PARA O RELATÓRIO DE FARMÁCIAS */
		MpmPrescricaoMdtoDAO prescricaoMdtoDAO = getPrescricaoMdtoDAO();
		String observacaoBanco = prescricaoMdtoDAO.obterObservacaoDoBanco(medicamentoSolucao.getId());
		medicamentoSolucao.setObservacao(observacaoBanco);
		/*---------------------------------------------------------------------------------------------*/
		
		if ((medicamentoSolucaoAnterior.getObservacao() != null && medicamentoSolucao.getObservacao() == null)
				|| (medicamentoSolucaoAnterior.getObservacao() == null && medicamentoSolucao.getObservacao() != null)
				|| (medicamentoSolucaoAnterior.getObservacao() != null && !medicamentoSolucaoAnterior
						.getObservacao().equals(
								medicamentoSolucao.getObservacao()))) {
			
			if (medicamentoSolucao.getObservacao() != null && medicamentoSolucaoAnterior.getObservacao() != null){
				
				descricaoAtualizacao.append(',').append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append("obs.: DE ").append(medicamentoSolucaoAnterior.getObservacao().replace("\n", QUEBRA_LINHA + CoreUtil.acrescentarEspacos(28)));

				descricaoAtualizacao.append(',').append(QUEBRA_LINHA)
						.append(CoreUtil.acrescentarEspacos(13))
						.append(CoreUtil.acrescentarEspacos(9)).append(PARA_)
						.append(medicamentoSolucao.getObservacao().replace("\n", QUEBRA_LINHA + CoreUtil.acrescentarEspacos(33)));
			}
			else{
				if (medicamentoSolucao.getObservacao() != null){
					descricaoAtualizacao.append(',').append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append(OBS).append(medicamentoSolucao.getObservacao().replace("\n", QUEBRA_LINHA + CoreUtil.acrescentarEspacos(22)));
				}
			}
		}
		else{
			if (medicamentoSolucao.getObservacao() != null){
				descricaoAtualizacao.append(',').append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append(OBS).append(medicamentoSolucao.getObservacao().replace("\n", QUEBRA_LINHA + CoreUtil.acrescentarEspacos(22)));
			}
		}
		
		if (medicamentoSolucao.getIndAntiMicrobiano()){
			descricaoAtualizacao.append(", ").append(obterDiaDeAdministracao(medicamentoSolucao)).append("; ");				
		}
		
		return descricaoAtualizacao.toString();
	}
	
	
	
	private String obterGotejoMedicamentoSolucao(MpmPrescricaoMdto medicamentoSolucao){
		String gotejo = null;
		if (medicamentoSolucao.getGotejo() != null) {
			Locale locBR = new Locale("pt", "BR");//Brasil 
	        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
	        dfSymbols.setDecimalSeparator(',');
	        DecimalFormat format = new DecimalFormat("#,###,###,###,##0.##", dfSymbols);
	        gotejo = format.format(medicamentoSolucao.getGotejo());
		}
		return gotejo;
	}
	
	private String obterTipoVelocAdministracaoMedicamentoSolucao(MpmPrescricaoMdto medicamentoSolucao){
		String descricaoTva = null;
		if (medicamentoSolucao.getGotejo() != null) {
			if (medicamentoSolucao.getTipoVelocAdministracao() != null) {
				descricaoTva = medicamentoSolucao
				.getTipoVelocAdministracao().getDescricao();
			} 
			else{
				descricaoTva = "ERRO: tipo velocidade administracao nao informado";
			}
		}
		
		return descricaoTva;
	}
	
	
	private String obterQtdeHorasCorrerMedicamentoSolucao(MpmPrescricaoMdto medicamentoSolucao){
		String qtdeHorasCorrer = null;
		
		if (medicamentoSolucao.getQtdeHorasCorrer() != null) {
			qtdeHorasCorrer = medicamentoSolucao.getQtdeHorasCorrer().toString();
		}
		
		return qtdeHorasCorrer;
	}

	private String obterUnidadeHorasCorrerMedicamentoSolucao(MpmPrescricaoMdto medicamentoSolucao){
		String unidadeHotasCorrer = null;
		
		if (medicamentoSolucao.getUnidHorasCorrer() == null
				|| DominioUnidadeHorasMinutos.H
						.equals(medicamentoSolucao.getUnidHorasCorrer())) {
			
			unidadeHotasCorrer = " horas ";
		} else {
			unidadeHotasCorrer = " minutos ";
		}
		
		return unidadeHotasCorrer;
	}
	
	private String obterDescricaoDiluenteMedicamentoSolucao(MpmPrescricaoMdto medicamentoSolucao){
		StringBuffer descricaoDiluente = null;
		
		if (medicamentoSolucao.getDiluente() != null) {
			descricaoDiluente = new StringBuffer(medicamentoSolucao.getDiluente().getDescricao());
			if (medicamentoSolucao.getDiluente().getConcentracaoFormatada() != null){
				descricaoDiluente.append(' ').append(medicamentoSolucao.getDiluente().getConcentracaoFormatada()).append(" MG/ML");
			}
		}
		
		return descricaoDiluente != null ? descricaoDiluente.toString() : null;
	}
	
	
	private String obterVolumeDiluenteMedicamentoSolucao(MpmPrescricaoMdto medicamentoSolucao){
		String volumeDiluente = getNumeroFormatado(medicamentoSolucao
				.getVolumeDiluenteMl(),
				VOLUME_DILUENTE_ML);
		return volumeDiluente;
	}

	private String obterFrequenciaFormatadaMedicamentoSolucao(MpmPrescricaoMdto medicamentoSolucao){
		StringBuffer frequencia = new StringBuffer();
		
		if (StringUtils.isNotBlank(medicamentoSolucao
				.getTipoFreqAprazamento().getSintaxe())) {
			
			if (medicamentoSolucao.getFrequencia() != null){
				frequencia.append(medicamentoSolucao
						.getTipoFreqAprazamento()
						.getSintaxeFormatada(medicamentoSolucao.getFrequencia()).toLowerCase());
			}
		} else {
			frequencia.append(medicamentoSolucao.getTipoFreqAprazamento()
					.getDescricao());
		}
		
		return frequencia.toString();
	}
	
	/**
	 * Método utilizado SOMENTE para o relatório de dispensação da farmácia -
	 * ALTERAÇÃO DE ITENS
	 * 
	 * @param medicamentoSolucao
	 * @param inclusaoExclusao
	 * @param isUpperCase
	 * @param incluirCodigoMedicamentos
	 * @return
	 */
	public List<ItemDispensacaoFarmaciaVO> obterDescricaoDosagemAlteracaoItensMedicamentoSolucaoDispensacaoFarmacia(
			MpmPrescricaoMdto medicamentoSolucao, Boolean isUpperCase){
			//Só para fazer o DE
		   	ItemDispensacaoFarmaciaVO itemDe = preparaItemDispensacaoFarmaciaVOVazio(_DE_MAIUSCULO);
		   	//Só para fazer o PARA
		   	ItemDispensacaoFarmaciaVO itemPara = preparaItemDispensacaoFarmaciaVOVazio(_PARA_);

		   	MpmPrescricaoMdto medicamentoSolucaoAnterior = medicamentoSolucao.getPrescricaoMdtoOrigem();
		    List<ItemDispensacaoFarmaciaVO> listaItensDispVO = new ArrayList<ItemDispensacaoFarmaciaVO>();
		    //Se for solução tem que juntar na listagem os itens antigos e os novos
		    if (medicamentoSolucao.getIndSolucao() != null && medicamentoSolucao.getIndSolucao()){
			   	//Adiciona o item DE
		    	listaItensDispVO.add(itemDe);
			   	//Adiciona os itens do medicamento antigo
			   	listaItensDispVO.addAll(this.obterDescricaoDosagemItensMedicamentoSolucaoDispensacaoFarmacia(medicamentoSolucaoAnterior, isUpperCase));
			   	//Só para fazer o PARA
			   	listaItensDispVO.add(itemPara);
			   	//Adiciona os itens do medicamento novo
			   	listaItensDispVO.addAll(this.obterDescricaoDosagemItensMedicamentoSolucaoDispensacaoFarmacia(medicamentoSolucao, isUpperCase));
		    }
		    else{//Se for um medicamento
		    	//Obtem as dosagens do item anterior e novo
			    final String medAdministracaoDosagemEditDe = obterAdministracaoDosagemItensMedicamentoSolucao(medicamentoSolucaoAnterior, false, false);
			    final String medAdministracaoDosagemEditPara = obterAdministracaoDosagemItensMedicamentoSolucao(medicamentoSolucao, false, false);
			   
			    //Se mudou a dosagem
			    if (!medAdministracaoDosagemEditDe.equals(medAdministracaoDosagemEditPara)) { 
			    	//Obtem o item do medicamento (não pode ter sido trocado)
			   	  	MpmItemPrescricaoMdto itemPrescricaoMdto = getPrescricaoMedicaFacade().obterItemMedicamentoNaoDiluente(medicamentoSolucao.getItensPrescricaoMdtos());
			   	  	ItemDispensacaoFarmaciaVO itemDispVO = new ItemDispensacaoFarmaciaVO();
			   	  	itemDispVO = this.popularItemDispensacaoFarmaciaVO(itemPrescricaoMdto, itemDispVO, itemPrescricaoMdto.getMedicamento().getDescricao());
			   	  	//Adiciona o item com código e quantidade
			   	  	listaItensDispVO.add(itemDispVO);
			   	  	//Adiciona o DE
			   	  	listaItensDispVO.add(itemDe);
			   	  	//Cria e adiciona a dostem anterior
			   	  	ItemDispensacaoFarmaciaVO dosagemDe = preparaItemDispensacaoFarmaciaVOVazio(medAdministracaoDosagemEditDe);
			   	  	listaItensDispVO.add(dosagemDe);
			   	  	//Adiciona o PARA
			   	  	listaItensDispVO.add(itemPara);
			   	  	//Cria e adiciona a dostem nova
			   	  	ItemDispensacaoFarmaciaVO dosagemPara = preparaItemDispensacaoFarmaciaVOVazio(medAdministracaoDosagemEditPara);
			   	  	listaItensDispVO.add(dosagemPara);
			    }
			    else{
			    	//No caso de não ter sido alterada a dosagem, não muda nada aqui.
			    	listaItensDispVO.addAll(obterDescricaoDosagemItensMedicamentoSolucaoDispensacaoFarmacia(medicamentoSolucao, isUpperCase));
			    }
				
		    }
		
		    return listaItensDispVO;
		
	}
	
	/**
	 * 
	 * @param descricao
	 */
	private ItemDispensacaoFarmaciaVO preparaItemDispensacaoFarmaciaVOVazio(String descricao){
		ItemDispensacaoFarmaciaVO item = new ItemDispensacaoFarmaciaVO();
		item.setCodigoItemMedicamento("");
		item.setDescricaoItemMedicamento(descricao);
		item.setQtdSolicitada("");
		return item;
	}
	
	/**
	 * Seta os atributos código, descrição e quantidade em um objeto ItemDispensacaoFarmaciaVO (sendo que a
	 * descrição já é passada por parâmetro) e retorna o objeto
	 * @param item
	 * @return
	 */
	private ItemDispensacaoFarmaciaVO popularItemDispensacaoFarmaciaVO(MpmItemPrescricaoMdto item, ItemDispensacaoFarmaciaVO itemVO, String descricao){
		AfaDispensacaoMdtos dispensacaoMdtos = getFarmaciaFacade().obterDispensacaoMdtosPorItemPrescricaoMdtoQtdSolicitada(item.getId());
		itemVO.setCodigoItemMedicamento(item.getId().getMedMatCodigo().toString());
		itemVO.setDescricaoItemMedicamento(descricao);
		if (dispensacaoMdtos.getQtdeSolicitada() != null) {
			if (item.getMedicamento().getTipoApresentacaoMedicamento() != null) {
				itemVO.setQtdSolicitada(dispensacaoMdtos.getQtdeSolicitada()+ " " + item.getMedicamento().getTipoApresentacaoMedicamento().getSigla());
			} else {
				itemVO.setQtdSolicitada(dispensacaoMdtos.getQtdeSolicitada().toString());
			}
		}
		return itemVO;
	}

	/**
	 * Método utilizado SOMENTE para o relatório de dispensação da farmácia
	 * 
	 * @param medicamentoSolucao
	 * @param inclusaoExclusao
	 * @param isUpperCase
	 * @param incluirCodigoMedicamentos
	 * @return
	 */
	public List<ItemDispensacaoFarmaciaVO> obterDescricaoDosagemItensMedicamentoSolucaoDispensacaoFarmacia(
			MpmPrescricaoMdto medicamentoSolucao, Boolean isUpperCase) {

		boolean diluenteEncontrado = false;
		List<ItemDispensacaoFarmaciaVO> listaItensDispVO = new ArrayList<ItemDispensacaoFarmaciaVO>();
		for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : medicamentoSolucao
				.getItensPrescricaoMdtos()) {
			StringBuffer descricaoConcentracao = new StringBuffer();
			ItemDispensacaoFarmaciaVO itemDispensacaoFarmaciaVO = new ItemDispensacaoFarmaciaVO();

			// Ajuste que foi necessário devido à melhoria #18867
			if (!medicamentoSolucao.getIndSolucao()) {
				if (medicamentoSolucao.getItensPrescricaoMdtos().size() > 1) {
					if (itemPrescricaoMedicamento.getMedicamento().equals(
							medicamentoSolucao.getDiluente())
							&& !diluenteEncontrado) {
						diluenteEncontrado = true;
						continue;
					}
				}
			}

			descricaoConcentracao.append(obterDescricaoDosagemItem(
					itemPrescricaoMedicamento, isUpperCase));

			// É um Medicamento
			if (medicamentoSolucao.getIndSolucao() == null
					|| !medicamentoSolucao.getIndSolucao()) {
				if (medicamentoSolucao.getViaAdministracao() != null) {
					descricaoConcentracao
							.append(", ")
							.append(medicamentoSolucao.getViaAdministracao()
									.getSigla()).append(", ");
				}
			}

			itemDispensacaoFarmaciaVO = this.popularItemDispensacaoFarmaciaVO(
					itemPrescricaoMedicamento, itemDispensacaoFarmaciaVO,
					descricaoConcentracao.toString());

			listaItensDispVO.add(itemDispensacaoFarmaciaVO);
		}

		return listaItensDispVO;

	}

	private String obterDescricaoDosagemItem(
			MpmItemPrescricaoMdto itemPrescricaoMedicamento, Boolean isUpperCase) {

		StringBuilder strBuilderFilho = new StringBuilder();

		strBuilderFilho.append(this.obterDescricaoMedicamento(itemPrescricaoMedicamento, isUpperCase));

		if (itemPrescricaoMedicamento.getMedicamento().getConcentracao() != null) {
			strBuilderFilho.append(' ').append(
					itemPrescricaoMedicamento.getMedicamento()
							.getConcentracaoFormatada());
		}
		if (itemPrescricaoMedicamento.getMedicamento()
				.getMpmUnidadeMedidaMedicas() != null
				&& StringUtils.isNotBlank(itemPrescricaoMedicamento
						.getMedicamento().getDescricaoUnidadeMedidaMedica())) {
			strBuilderFilho.append(' ').append(
					itemPrescricaoMedicamento.getMedicamento()
							.getDescricaoUnidadeMedidaMedica());
		}
		if (StringUtils.isNotBlank(itemPrescricaoMedicamento.getObservacao())) {
			strBuilderFilho.append(_DOIS_PONTOS_).append(
					itemPrescricaoMedicamento.getObservacao());
		}

		String numFormated = null;
		if (itemPrescricaoMedicamento.getDose() != null) {
			numFormated = AghuNumberFormat.formatarValor(
					itemPrescricaoMedicamento.getDose(),
					itemPrescricaoMedicamento.getClass(), DOSE);
		}

		strBuilderFilho
				.append("- " + getLabelSolucao(itemPrescricaoMedicamento) + " ")
				.append(numFormated).append(' ');

		if (itemPrescricaoMedicamento.getFormaDosagem() != null
				&& StringUtils.isNotBlank(itemPrescricaoMedicamento
						.getFormaDosagem().getDescricaoUnidadeMedidaMedica())) {
			strBuilderFilho.append(itemPrescricaoMedicamento.getFormaDosagem()
					.getDescricaoUnidadeMedidaMedica());
		} else if (itemPrescricaoMedicamento.getMedicamento()
				.getTipoApresentacaoMedicamento() != null) {
			strBuilderFilho.append(itemPrescricaoMedicamento.getMedicamento()
					.getTipoApresentacaoMedicamento().getSigla());
		}

		return strBuilderFilho.toString();
	}
	
	
	private String obterDescricaoMedicamento(MpmItemPrescricaoMdto itemPrescricaoMedicamento, Boolean isUpperCase){
		
		//Melhoria em Produção #54544 - Definição de medicamentos que não devem
		if(MEDICAMENTOS_DESCRICAO_NORMAL.contains(itemPrescricaoMedicamento.getMedicamento().getCodigo())){
			return itemPrescricaoMedicamento.getMedicamento().getDescricao();
		}
		else{
			// Por regra do SQL this.getMedicamento() nao deveria ser nulo.
			if (isUpperCase) {
				return itemPrescricaoMedicamento.getMedicamento().getDescricao().toUpperCase();
			} else {
				return itemPrescricaoMedicamento.getMedicamento().getDescricao().charAt(0)+itemPrescricaoMedicamento.getMedicamento().getDescricao().substring(1).toLowerCase();
			}
		}
	}
	
	/**
	 *
	 * @param medicamentoSolucao
	 * @param impressaoTotal
	 * @param inclusaoExclusao
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private String obterDescricaoDosagemItensMedicamentoSolucao(
			MpmPrescricaoMdto medicamentoSolucao, Boolean impressaoTotal,
			Boolean inclusaoExclusao, Boolean isUpperCase) {
		
		StringBuffer descricaoConcentracao = new StringBuffer();
		
		medicamentoSolucao = this.getPrescricaoMdtoDAO().merge(medicamentoSolucao);
		
		int contador = 1;
		boolean diluenteEncontrado = false;
		for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : medicamentoSolucao
				.getItensPrescricaoMdtos()) {
			
			//Ajuste que foi necessário devido à melhoria #18867
			if(!medicamentoSolucao.getIndSolucao()){
				if (medicamentoSolucao.getItensPrescricaoMdtos().size() > 1){
					if(itemPrescricaoMedicamento.getMedicamento().equals(medicamentoSolucao.getDiluente()) && !diluenteEncontrado) {
						diluenteEncontrado = true;
						continue;
					}
				}
				
			}
			
			StringBuilder strBuilderFilho = new StringBuilder();
			
			if (!impressaoTotal) {
				// Apenas medicamento.
				if (contador > 1 && !inclusaoExclusao){// && (medicamentoSolucao.getIndSolucao() == null || !medicamentoSolucao.getIndSolucao()) ){
					strBuilderFilho.append(CoreUtil.acrescentarEspacos(15));
				} else {
					strBuilderFilho.append(CoreUtil.acrescentarEspacos(2));							
				}
			}
			
			strBuilderFilho.append(this.obterDescricaoMedicamento(itemPrescricaoMedicamento, isUpperCase));
			
			if (itemPrescricaoMedicamento.getMedicamento().getConcentracao() != null) {
				strBuilderFilho.append(' ').append(
						itemPrescricaoMedicamento.getMedicamento()
								.getConcentracaoFormatada());
			}
			if (itemPrescricaoMedicamento.getMedicamento()
					.getMpmUnidadeMedidaMedicas() != null
					&& StringUtils
							.isNotBlank(itemPrescricaoMedicamento
									.getMedicamento()
									.getDescricaoUnidadeMedidaMedica())) {
				strBuilderFilho.append(' ').append(
						itemPrescricaoMedicamento.getMedicamento()
								.getDescricaoUnidadeMedidaMedica());
			}
			if (StringUtils.isNotBlank(itemPrescricaoMedicamento
					.getObservacao())) {
				strBuilderFilho.append(_DOIS_PONTOS_).append(
						itemPrescricaoMedicamento.getObservacao());
			}

			
			String numFormated = null;
			if (itemPrescricaoMedicamento.getDose() != null) {
				numFormated = AghuNumberFormat.formatarValor(itemPrescricaoMedicamento.getDose(), itemPrescricaoMedicamento.getClass(), DOSE);
			}
			
			strBuilderFilho.append("- ").append(getLabelSolucao(itemPrescricaoMedicamento)).append(' ').append(numFormated).append(' ');

			if (itemPrescricaoMedicamento.getFormaDosagem() != null
					&& StringUtils.isNotBlank(itemPrescricaoMedicamento
							.getFormaDosagem()
							.getDescricaoUnidadeMedidaMedica())) {
				strBuilderFilho.append(
						itemPrescricaoMedicamento.getFormaDosagem()
								.getDescricaoUnidadeMedidaMedica());
			} else if(itemPrescricaoMedicamento.getMedicamento().getTipoApresentacaoMedicamento() != null) {
				strBuilderFilho.append(
						itemPrescricaoMedicamento.getMedicamento()
								.getTipoApresentacaoMedicamento().getSigla());
			}
			
			// É um Medicamento
			if(medicamentoSolucao.getIndSolucao() == null || !medicamentoSolucao.getIndSolucao()){
				if(medicamentoSolucao.getViaAdministracao()!=null){
					strBuilderFilho.append(", ").append(
						medicamentoSolucao.getViaAdministracao().getSigla())
						.append(", ");
				}
			}
			
//			if (itemPrescricaoMedicamento.getIndAntiMicrobiano()){
//				strBuilderFilho.append(obterDiaDeAdministracao(medicamentoSolucao)).append(", ");				
//			}
			
			if (medicamentoSolucao.getIndSolucao()) {
				strBuilderFilho.append(QUEBRA_LINHA);
			}
			
			descricaoConcentracao.append(strBuilderFilho);
			
			contador ++;
		}
		
		return descricaoConcentracao.toString();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private String obterDescricaoAlteradaDosagemItensMedicamentoSolucao(
			MpmPrescricaoMdto medicamentoSolucao, Boolean impressaoTotal,
			Boolean inclusaoExclusao, Boolean isUpperCase) {

		StringBuffer descricaoConcentracao = new StringBuffer();

		int contador = 1;
		boolean diluenteEncontrado = false;
		for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : medicamentoSolucao
				.getItensPrescricaoMdtos()) {
			
			//Ajuste que foi necessário devido à melhoria #18867
			if(!medicamentoSolucao.getIndSolucao()){
				if (medicamentoSolucao.getItensPrescricaoMdtos().size() > 1){
					if(itemPrescricaoMedicamento.getMedicamento().equals(medicamentoSolucao.getDiluente()) && !diluenteEncontrado) {
						diluenteEncontrado = true;
						continue;
					}
				}
				
			}

			StringBuilder strBuilderFilho = new StringBuilder();

			if (!impressaoTotal) {
				if (contador == 1) {
					strBuilderFilho.append(CoreUtil.acrescentarEspacos(2));
				} else {
					strBuilderFilho.append(CoreUtil.acrescentarEspacos(13));
				}
			}

			strBuilderFilho.append(this.obterDescricaoMedicamento(itemPrescricaoMedicamento, isUpperCase));			

			if (itemPrescricaoMedicamento.getMedicamento().getConcentracao() != null) {
				strBuilderFilho.append(' ').append(
						itemPrescricaoMedicamento.getMedicamento()
								.getConcentracaoFormatada());
			}
			if (itemPrescricaoMedicamento.getMedicamento()
					.getMpmUnidadeMedidaMedicas() != null
					&& StringUtils
							.isNotBlank(itemPrescricaoMedicamento
									.getMedicamento()
									.getDescricaoUnidadeMedidaMedica())) {
				strBuilderFilho.append(' ').append(
						itemPrescricaoMedicamento.getMedicamento()
								.getDescricaoUnidadeMedidaMedica());
			}
			if (StringUtils.isNotBlank(itemPrescricaoMedicamento
					.getObservacao())) {
				strBuilderFilho.append(_DOIS_PONTOS_).append(
						itemPrescricaoMedicamento.getObservacao());
			}

			String numFormated = null;
			if (itemPrescricaoMedicamento.getDose() != null) {
				numFormated = AghuNumberFormat.formatarValor(
						itemPrescricaoMedicamento.getDose(),
						itemPrescricaoMedicamento.getClass(), DOSE);
			}

			strBuilderFilho.append(" - "+getLabelSolucao(itemPrescricaoMedicamento)+" ").append(numFormated)
					.append(' ');

			if (itemPrescricaoMedicamento.getFormaDosagem() != null
					&& StringUtils.isNotBlank(itemPrescricaoMedicamento
							.getFormaDosagem()
							.getDescricaoUnidadeMedidaMedica())) {
				strBuilderFilho.append(
						itemPrescricaoMedicamento.getFormaDosagem()
								.getDescricaoUnidadeMedidaMedica()).append(',');
			} else if (itemPrescricaoMedicamento.getMedicamento()
					.getTipoApresentacaoMedicamento() != null) {
				strBuilderFilho.append(
						itemPrescricaoMedicamento.getMedicamento()
								.getTipoApresentacaoMedicamento().getSigla());
			}
			/*if (medicamentoSolucao.getViaAdministracao() != null) {
				strBuilderFilho.append(' ').append(
						medicamentoSolucao.getViaAdministracao().getSigla())
						.append(", ");
			}

			if (itemPrescricaoMedicamento.getIndAntiMicrobiano()) {
				strBuilderFilho
						.append(obterDiaDeAdministracao(medicamentoSolucao));
			}
			*/
			if (medicamentoSolucao.getIndSolucao()) {
				strBuilderFilho.append(QUEBRA_LINHA);
			}
			descricaoConcentracao.append(strBuilderFilho);
			contador++;
		}

		return descricaoConcentracao.toString();
	}
	
	
	private String getLabelSolucao(MpmItemPrescricaoMdto itemPrescricaoMedicamento) {
		if (itemPrescricaoMedicamento.getPrescricaoMedicamento().getIndSolucao()) {
			return LABEL_DILUIR;
		} else {
			return LABEL_ADMINISTRAR;
		}
	}

	
	/** Método que obtem a descrição do item medicamento sem a dosagem
	 * 
	 * @param medicamentoSolucao
	 * @param impressaoTotal
	 * @param inclusaoExclusao
	 * @return
	 */
	private String obterDescricaoSemDosagemItensMedicamentoSolucao(
			MpmPrescricaoMdto medicamentoSolucao, Boolean impressaoTotal,
			Boolean inclusaoExclusao, Boolean isUpperCase) {

		StringBuffer descricaoConcentracao = new StringBuffer();

		int contador = 1;
		boolean diluenteEncontrado = false;
		for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : medicamentoSolucao
				.getItensPrescricaoMdtos()) {
			
			//Ajuste que foi necessário devido à melhoria #18867
			if(!medicamentoSolucao.getIndSolucao()){
				if (medicamentoSolucao.getItensPrescricaoMdtos().size() > 1){
					if(itemPrescricaoMedicamento.getMedicamento().equals(medicamentoSolucao.getDiluente()) && !diluenteEncontrado) {
						diluenteEncontrado = true;
						continue;
					}
				}
				
			}

			StringBuilder strBuilderFilho = new StringBuilder();

			if (!impressaoTotal) {
				if (contador == 1) {
					strBuilderFilho.append(CoreUtil.acrescentarEspacos(2));
				} else {
					strBuilderFilho.append(CoreUtil.acrescentarEspacos(13));
				}
			}

			strBuilderFilho.append(this.obterDescricaoMedicamento(itemPrescricaoMedicamento, isUpperCase));
			
			if (itemPrescricaoMedicamento.getMedicamento().getConcentracao() != null) {
				strBuilderFilho.append(' ').append(
						itemPrescricaoMedicamento.getMedicamento()
								.getConcentracaoFormatada());
			}
			if (itemPrescricaoMedicamento.getMedicamento()
					.getMpmUnidadeMedidaMedicas() != null
					&& StringUtils
							.isNotBlank(itemPrescricaoMedicamento
									.getMedicamento()
									.getDescricaoUnidadeMedidaMedica())) {
				strBuilderFilho.append(' ').append(
						itemPrescricaoMedicamento.getMedicamento()
								.getDescricaoUnidadeMedidaMedica());
			}
			if (StringUtils.isNotBlank(itemPrescricaoMedicamento
					.getObservacao())) {
				strBuilderFilho.append(_DOIS_PONTOS_).append(
						itemPrescricaoMedicamento.getObservacao());
			}
			descricaoConcentracao.append(strBuilderFilho);
			contador++;
		}
	//	descricaoConcentracao = " -" + descricaoConcentracao;
		return descricaoConcentracao.toString();
	}
	
	/**
	 * Método que obtem a dosagem do item de medicamento
	 * @param medicamentoSolucao
	 * @param impressaoTotal
	 * @param inclusaoExclusao
	 * @return
	 */
	private String obterAdministracaoDosagemItensMedicamentoSolucao(
			MpmPrescricaoMdto medicamentoSolucao, Boolean impressaoTotal,
			Boolean inclusaoExclusao) {

		StringBuffer descricaoConcentracao = new StringBuffer();

		boolean diluenteEncontrado = false;
		for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : medicamentoSolucao
				.getItensPrescricaoMdtos()) {

			//Ajuste que foi necessário devido à melhoria #18867
			if(!medicamentoSolucao.getIndSolucao()){
				if (medicamentoSolucao.getItensPrescricaoMdtos().size() > 1){
					if(itemPrescricaoMedicamento.getMedicamento().equals(medicamentoSolucao.getDiluente()) && !diluenteEncontrado) {
						diluenteEncontrado = true;
						continue;
					}
				}
			}
			StringBuilder strBuilderFilho = new StringBuilder();
			
			String numFormated = null;
			if (itemPrescricaoMedicamento.getDose() != null) {
				numFormated = AghuNumberFormat.formatarValor(
						itemPrescricaoMedicamento.getDose(),
						itemPrescricaoMedicamento.getClass(), DOSE);
			}

			strBuilderFilho.append("Administrar ").append(numFormated)
					.append(' ');

			if (itemPrescricaoMedicamento.getFormaDosagem() != null
					&& StringUtils.isNotBlank(itemPrescricaoMedicamento
							.getFormaDosagem()
							.getDescricaoUnidadeMedidaMedica())) {
				strBuilderFilho.append(
						itemPrescricaoMedicamento.getFormaDosagem()
								.getDescricaoUnidadeMedidaMedica());
			} else if (itemPrescricaoMedicamento.getMedicamento()
					.getTipoApresentacaoMedicamento() != null) {
				strBuilderFilho.append(
						itemPrescricaoMedicamento.getMedicamento()
								.getTipoApresentacaoMedicamento().getSigla());
			}
			descricaoConcentracao.append(strBuilderFilho);
		}

		return descricaoConcentracao.toString();
	}

	private String obterDiaDeAdministracao(MpmPrescricaoMdto medicamentoSolucao) {
		StringBuffer diaAdministracao = new StringBuffer();
		
		if (medicamentoSolucao.getIndAntiMicrobiano() 
				&& medicamentoSolucao.getDthrInicioTratamento() != null){
			
			if (medicamentoSolucao.getIndAntiMicrobiano()
					&& medicamentoSolucao.getDthrInicioTratamento() != null
					&& medicamentoSolucao.getPrescricaoMedica() != null
					&& medicamentoSolucao.getPrescricaoMedica()
							.getDtReferencia() != null) {

				Date dtPmeInicioVigencia = medicamentoSolucao
						.getPrescricaoMedica().getDtReferencia();
//				Date dtGeralRefInicioVigencia = null;// this.getDthrInicioTratamento();
//
//				if (medicamentoSolucao.getPrescricaoMedica().getAtendimento() != null) {
//					MpmPrescricaoMedica p = medicamentoSolucao
//							.getPrescricaoMedica().getAtendimento()
//							.getPrescricaoMedicaInicioAtendimento(
//									medicamentoSolucao
//											.getDthrInicioTratamento());
//					if (p != null) {
//						dtGeralRefInicioVigencia = p.getDtReferencia();
//					}
//				}
//
//				Integer tempoDuracao;
//				if (dtGeralRefInicioVigencia == null) {
//					tempoDuracao = 0;
//				} else {
//					tempoDuracao = DateUtil.diffInDaysInteger(
//							dtPmeInicioVigencia, dtGeralRefInicioVigencia);
//				}
//				if (tempoDuracao.compareTo(0) < 0) {
//					tempoDuracao = 0;
//				}
				diaAdministracao.append(" Dia de Administração: ").append(getTempoDuracao(dtPmeInicioVigencia, medicamentoSolucao.getDthrInicioTratamento()));
			}
		}
		
		return diaAdministracao.toString();
	}
	
	
	private int getTempoDuracao(Date dtRefPrescricaoMedica, Date dataInicioTratamento) {
		Integer tempoDuracao = DateUtil.diffInDaysInteger(dtRefPrescricaoMedica, dataInicioTratamento);
		if (dataInicioTratamento == null || tempoDuracao < 0) {
			tempoDuracao = 0;
		}
		
		return tempoDuracao.intValue();
	}
	
	/**
	 * 
	 * @param consultoria
	 * @return
	 */
	public String obterDescricaoFormatadaConsultoria(Integer atdSeq, Integer seq) {
		
		MpmSolicitacaoConsultoria consultoria = mpmSolicitacaoConsultoriaDAO.obterPorChavePrimaria(new MpmSolicitacaoConsultoriaId(atdSeq, seq));
		final String FIELD_TIPO_CONSULTORIA = "Consultoria";
		final String FIELD_TIPO_OUTROS = "Avaliação Pré-Cirúrgica";
		final String FIELD_INDICADOR_URGENTE_SIM = "URGENTE em";
		final String FIELD_INDICADOR_URGENTE_NAO = "em";

		StringBuffer descricao = new StringBuffer();
		if (DominioTipoSolicitacaoConsultoria.C == consultoria.getTipo()) {
			descricao.append(FIELD_TIPO_CONSULTORIA);
		} else {
			descricao.append(FIELD_TIPO_OUTROS);
		}
		if (consultoria.isUrgente()) {
			descricao.append(' ').append(FIELD_INDICADOR_URGENTE_SIM);
		} else {
			descricao.append(' ').append(FIELD_INDICADOR_URGENTE_NAO);
		}
		descricao.append(' ')
				.append(consultoria.getEspecialidade().getNomeEspecialidade());
		return StringUtils.capitalize(descricao.toString().trim().toLowerCase());

	}
	
	/**
	 * Método que retorna a descrição indicando o que foi alterado em uma
	 * solicitação de consultoria
	 *
	 * @param consultoria
	 * @return
	 */
	public String obterDescricaoAlteracaoConsultoria(final Integer atdSeq, final Integer seq) {
		final MpmSolicitacaoConsultoria consultoria = mpmSolicitacaoConsultoriaDAO.obterPorChavePrimaria(new MpmSolicitacaoConsultoriaId(atdSeq, seq));
		StringBuffer descricaoAtualizacao = new StringBuffer(30);
		
		MpmSolicitacaoConsultoria consultoriaAnterior = consultoria.getSolicitacaoConsultoriaOriginal();
		
		//Verifica se alterou o Tipo da Consultoria
		if (!consultoriaAnterior.getTipo().equals(consultoria.getTipo())){
			if (consultoriaAnterior.getTipo().equals(
					DominioTipoSolicitacaoConsultoria.C)){
				descricaoAtualizacao.append("DE Consultoria ");
			} else{
				descricaoAtualizacao.append("DE Avaliação Pré-Cirúrgica ");
			}
			if (consultoria.getTipo().equals(DominioTipoSolicitacaoConsultoria.C)){
				descricaoAtualizacao.append("PARA Consultoria ");
			}
			else{
				descricaoAtualizacao.append("PARA Avaliação Pré-Cirúrgica ");
			}
		}
		else{
			if (consultoria.getTipo().equals(DominioTipoSolicitacaoConsultoria.C)){
				descricaoAtualizacao.append("Consultoria ");
			}
			else{
				descricaoAtualizacao.append("Avaliação Pré-Cirúrgica ");
			}
		}
		
		//Verifica se alterou o indicador de urgência
		if (!consultoriaAnterior.getIndUrgencia().equals(consultoria.getIndUrgencia())){
			descricaoAtualizacao.append(", ");
			if (DominioSimNao.S.equals(consultoriaAnterior.getIndUrgencia())){
				descricaoAtualizacao.append("DE URGENTE ");
			}
			else{
				descricaoAtualizacao.append("DE [] ");
			}
			if (DominioSimNao.S.equals(consultoria.getIndUrgencia())){
				descricaoAtualizacao.append("PARA URGENTE ");
			}
			else{
				descricaoAtualizacao.append("PARA [] ");
			}
		}
		else{
			if (DominioSimNao.S.equals(consultoria.getIndUrgencia())){
				descricaoAtualizacao.append("URGENTE ");
			}
			else{
				descricaoAtualizacao.append(' ');
			}
		}
		
		//Verifica se alterou a especialidade
		if (!consultoriaAnterior.getEspecialidade().equals(consultoria.getEspecialidade())){
			descricaoAtualizacao.append(", ");
			descricaoAtualizacao.append("DE ")
					.append(consultoriaAnterior.getEspecialidade()
							.getNomeEspecialidade()).append(_PARA_)
					.append(consultoria.getEspecialidade().getNomeEspecialidade());
		}
		else{
			descricaoAtualizacao.append("em ").append(consultoria.getEspecialidade().getNomeEspecialidade());
		}

		
		return descricaoAtualizacao.toString();
	}

	/**
	 * 
	 * @param solicitacaoHemoterapica
	 * @return
	 */

	public String obterDecricaoformatadaSolicitacoesHemoterapicas(Integer atdSeq, Integer seq, Boolean impressaoTotal, Boolean inclusaoExclusao){
		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = absSolicitacoesHemoterapicasDAO.obterPorChavePrimaria(new AbsSolicitacoesHemoterapicasId(atdSeq, seq));

		StringBuilder descricao = new StringBuilder(80);

		descricao.append(obterDescricaoItensSolicitacaoHemoterapica(solicitacaoHemoterapica, impressaoTotal, inclusaoExclusao));
		
		if ((solicitacaoHemoterapica.getIndUrgente() != null && solicitacaoHemoterapica
				.getIndUrgente().booleanValue())
				|| solicitacaoHemoterapica.getIndPacTransplantado()
						.booleanValue()
				|| solicitacaoHemoterapica.getIndTransfAnteriores()
						.booleanValue()) {
			
			descricao.append(QUEBRA_LINHA);
		}

		if (solicitacaoHemoterapica.getIndUrgente() != null
				&& solicitacaoHemoterapica.getIndUrgente().booleanValue()) {
			descricao.append(" Urgente, ");
		}

		if (solicitacaoHemoterapica.getIndPacTransplantado().booleanValue()) {
			descricao.append(" Paciente Transplantado, ");
		}

		if (solicitacaoHemoterapica.getIndTransfAnteriores().booleanValue()) {
			descricao.append(" Transfusões nos últimos 3 dias, ");
		}

		if (solicitacaoHemoterapica.getObservacao() != null) {
			descricao
					.append(QUEBRA_LINHA+CoreUtil.acrescentarEspacos(13)+ "Obs.: "	
					+ solicitacaoHemoterapica.getObservacao().replace("\n", QUEBRA_LINHA + CoreUtil.acrescentarEspacos(23)));
		}

		return descricao.toString().trim();

	}
	
	/**
	 * Obtém a descrição dos ítens da solicitação hemoterapica
	 *
	 * @param solicitacaoHemoterapica
	 * @return
	 */
	private String obterDescricaoItensSolicitacaoHemoterapica(
			AbsSolicitacoesHemoterapicas solicitacaoHemoterapica,
			Boolean impressaoTotal, Boolean inclusaoExclusao) {
		
		String retorno = "";
		StringBuilder descricao = new StringBuilder();
		
		int contador = 1;
		for (AbsItensSolHemoterapicas aishe : solicitacaoHemoterapica
				.getItensSolHemoterapicas()) {
			

			StringBuilder descricaoItem = new StringBuilder(30);
			
			if (!impressaoTotal){
				if (!inclusaoExclusao){
					if (contador > 1){
						descricaoItem.append(CoreUtil.acrescentarEspacos(13));
					}					
				}
			}		

			descricaoItem.append(StringUtils.capitalize(aishe.getDescricao()
					.toLowerCase()));

			Byte valor = 1;
			if (aishe.getQtdeUnidades() != null) {
				if (aishe.getQtdeUnidades().equals(valor)) {
					descricaoItem.append(' ').append(
							aishe.getQtdeUnidades().toString()).append(
							" unidade,");
				} else {
					descricaoItem.append(' ').append(
							aishe.getQtdeUnidades().toString()).append(
							" unidades,");
				}
			}

			if (aishe.getQtdeMl() != null) {
				descricaoItem.append(' ').append(aishe.getQtdeMl()).append(
						" ml,");
			}

			descricaoItem.append(aishe.getIndicadores());

			if (aishe.getTipoFreqAprazamento() != null) {
				if (aishe.getTipoFreqAprazamento().getSintaxe() != null) {
					// descricao += " " +
					// this.getTipoFreqAprazamento().getSintaxe() + " # " +
					// this.frequencia.toString();
					descricaoItem.append(' ').append(
							aishe.getTipoFreqAprazamento().getSintaxeFormatada(
									aishe.getFrequencia()).toLowerCase());
					descricaoItem.append(", ");
				} else if (aishe.getTipoFreqAprazamento().getDescricao() != null) {
					descricaoItem.append(' ').append(
							aishe.getTipoFreqAprazamento().getDescricao());
					descricaoItem.append(", ");
				}

			}

			if (aishe.getQtdeAplicacoes() != null) {
				descricaoItem.append(' ').append(
						aishe.getQtdeAplicacoes().toString()).append(
						" aplicações,");
			}

			descricaoItem.append(QUEBRA_LINHA);

			descricao.append(descricaoItem.toString());
			contador++;
		}
		
		retorno = descricao.toString();
		
		if (retorno.length() >= 5){
			retorno = retorno.substring(0, retorno.length() - 5); // remove o último <br>			
		}
		
		return retorno;
	}
	
	/**
	 * Método que retorna a descrição indicando o que foi alterado em uma
	 * solicitação de hemoterapia
	 *
	 * @param solicitacaoHemoterapica
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String obterDecricaoAlteracaoSolicitacoesHemoterapicas(Integer atdSeq, Integer seq, Boolean impressaoTotal){
		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = absSolicitacoesHemoterapicasDAO.obterPorChavePrimaria(new AbsSolicitacoesHemoterapicasId(atdSeq, seq)); 
		StringBuffer descricaoAtualizacao = new StringBuffer(50);
		AbsSolicitacoesHemoterapicas solicitacaoHemoterapicaAntiga = solicitacaoHemoterapica.getSolicitacaoHemoterapica();
		
		//SINTAXE ISH DE
		descricaoAtualizacao.append("DE").append(CoreUtil.acrescentarEspacos(8))
		.append(obterDescricaoItensSolicitacaoHemoterapica(solicitacaoHemoterapicaAntiga, impressaoTotal, false))
		.append(QUEBRA_LINHA)
		//SINTAXE ISH PARA
		.append("PARA").append(CoreUtil.acrescentarEspacos(3))
		.append(obterDescricaoItensSolicitacaoHemoterapica(solicitacaoHemoterapica, impressaoTotal, false));
		//descricaoAtualizacao += QUEBRA_LINHA;
		
		/* ---- SINTAXE SHE ---- */
		
		//Urgente
		if ((solicitacaoHemoterapica.getIndUrgente()!= null && solicitacaoHemoterapicaAntiga.getIndUrgente() == null)
				|| (solicitacaoHemoterapica.getIndUrgente() == null && solicitacaoHemoterapicaAntiga.getIndUrgente() != null)
				|| (solicitacaoHemoterapica.getIndUrgente() != null && !solicitacaoHemoterapica
						.getIndUrgente().equals(
								solicitacaoHemoterapicaAntiga.getIndUrgente()))) {
			
			descricaoAtualizacao.append(QUEBRA_LINHA);
			
			if (solicitacaoHemoterapicaAntiga.getIndUrgente() != null && solicitacaoHemoterapicaAntiga.getIndUrgente()){
				descricaoAtualizacao.append("DE Urgente ");
			} else{
				descricaoAtualizacao.append("DE não Urgente ");
			}
			
			if (solicitacaoHemoterapica.getIndUrgente() != null && solicitacaoHemoterapica.getIndUrgente()){
				descricaoAtualizacao.append(" PARA Urgente, ");
			} else{
				descricaoAtualizacao.append(" PARA não Urgente, ");
			}
		} else{
			if (solicitacaoHemoterapica.getIndUrgente() != null && solicitacaoHemoterapica.getIndUrgente()){
				descricaoAtualizacao.append(" Urgente, ");
			}
		}
		
		//Paciente Transplantado
		if (!solicitacaoHemoterapicaAntiga.getIndPacTransplantado().equals(
				solicitacaoHemoterapica.getIndPacTransplantado())) {
			
			if (solicitacaoHemoterapicaAntiga.getIndPacTransplantado()){
				descricaoAtualizacao.append(" DE Paciente Transplantado ");
			} else{
				descricaoAtualizacao.append(" DE Paciente não Transplantado ");
			}
			
			if (solicitacaoHemoterapica.getIndPacTransplantado()){
				descricaoAtualizacao.append(" PARA Paciente Transplantado, ");
			} else{
				descricaoAtualizacao.append(" PARA Paciente não Transplantado, ");
			}
		} else{
			if (solicitacaoHemoterapica.getIndPacTransplantado()){
				descricaoAtualizacao.append(" Paciente Transplantado, ");
			}
		}
		
		//Observação
		if (solicitacaoHemoterapicaAntiga.getObservacao() != null
				|| solicitacaoHemoterapica.getObservacao() != null) {
		
			if ((solicitacaoHemoterapicaAntiga.getObservacao() != null && solicitacaoHemoterapica
					.getObservacao() == null)
					|| (solicitacaoHemoterapicaAntiga.getObservacao() == null && solicitacaoHemoterapica
							.getObservacao() != null)
					|| (solicitacaoHemoterapicaAntiga.getObservacao() != null && !solicitacaoHemoterapicaAntiga
							.getObservacao().equals(
									solicitacaoHemoterapica.getObservacao()))) {
				
				if (StringUtils.isNotBlank(solicitacaoHemoterapicaAntiga
						.getObservacao())){
					descricaoAtualizacao.append(QUEBRA_LINHA) 
							.append(CoreUtil.acrescentarEspacos(13)) 
							.append("obs.: DE ") 
							.append(solicitacaoHemoterapicaAntiga
									.getObservacao()
									.replace(
											"\n", 
											QUEBRA_LINHA 
													+ CoreUtil
															.acrescentarEspacos(28)));
				} else{
					descricaoAtualizacao.append(QUEBRA_LINHA) 
							.append(CoreUtil.acrescentarEspacos(13)).append("obs.: DE[] ");
				}
				if (StringUtils.isNotBlank(solicitacaoHemoterapica.getObservacao())){
					descricaoAtualizacao.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(22)).append(PARA_).append(solicitacaoHemoterapica.getObservacao().replace("\n", QUEBRA_LINHA +CoreUtil.acrescentarEspacos(33)));
				} else{
					descricaoAtualizacao.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(22)).append("PARA [] ");
				}
			} else{
				if (StringUtils.isNotBlank(solicitacaoHemoterapica.getObservacao())){
					if (!descricaoAtualizacao.equals("")){
						descricaoAtualizacao.append(CoreUtil.acrescentarEspacos(13));
					}
					descricaoAtualizacao.append(QUEBRA_LINHA).append(CoreUtil.acrescentarEspacos(13)).append(OBS).append(solicitacaoHemoterapica.getObservacao().replace("\n", QUEBRA_LINHA + CoreUtil.acrescentarEspacos(20)));
				}
			}
		}
		
		return descricaoAtualizacao.toString();
	}
	
	

	/**
	 * 
	 * @param procedimento
	 * @return
	 */
	public String obterDecricaoFormatadaProcedimento(final Integer atdSeq, final Long seq, Boolean impressaoTotal, Boolean inclusaoExclusao) {
		MpmPrescricaoProcedimento procedimento = mpmPrescricaoProcedimentoDAO.obterPorChavePrimaria(new MpmPrescricaoProcedimentoId(atdSeq, seq));

		StringBuilder descricao = new StringBuilder("");

		if (procedimento.getProcedimentoEspecialDiverso() != null) {
			descricao.append(StringUtils.capitalize(procedimento.getProcedimentoEspecialDiverso().getDescricao().toLowerCase()));
		}
		if (procedimento.getProcedimentoCirurgico() != null) {
			descricao.append(' ').append(
					StringUtils.capitalize(procedimento.getProcedimentoCirurgico().getDescricao().toLowerCase()));
		}
		if (procedimento.getMatCodigo() != null) {
			descricao.append(' ').append(StringUtils.capitalize(procedimento.getMatCodigo().getNome().toLowerCase()));
		}
		//descricao.append("; ");

		if (procedimento.getQuantidade() != null
				&& procedimento.getQuantidade() > 0) {
			descricao.append(' ').append(
					procedimento.getQuantidade().toString());
			if (procedimento.getMatCodigo() != null) {
				descricao.append(' ').append(
						procedimento.getMatCodigo().getUmdCodigo());
			}
			descricao.append("; ");
		}

		// Monta string dos filhos.
		String descricaoFilhos = obterDescricaoFilhosProcedimento(procedimento,
				impressaoTotal, inclusaoExclusao);
		if (StringUtils.isNotBlank(descricaoFilhos)){
			descricao.append(QUEBRA_LINHA);
		}
		descricao.append(descricaoFilhos);

		if (StringUtils.isNotBlank(procedimento.getInformacaoComplementar())) {
			descricao.append(QUEBRA_LINHA + "Inf. Complementares=").append(
					procedimento.getInformacaoComplementar().toUpperCase());
		}

		return descricao.toString().trim();

	}
	
	/**
	 * Obtém a descrição dos filhos do procedimento
	 *
	 * @param procedimento
	 * @return
	 */
	private String obterDescricaoFilhosProcedimento(
			MpmPrescricaoProcedimento procedimento, Boolean impressaoTotal,
			Boolean inclusaoExclusao) {
		
//		MpmModoUsoPrescProcedDAO modoUsoPrescProcedDAO = getModoUsoPrescProcedDAO();
//		List<MpmModoUsoPrescProced> listaModoUsos = modoUsoPrescProcedDAO
//				.obterPrescricaoProcedimentoPorAtendimentoESeq(procedimento
//						.getId().getSeq(), procedimento.getId().getAtdSeq());
		
		StringBuffer descricao = new StringBuffer();
		
		int contador = 1;
		for (MpmModoUsoPrescProced modoUsoProceds : procedimento.getModoUsoPrescricaoProcedimentos()) {

			StringBuffer descricaoItem = new StringBuffer();

			if (modoUsoProceds.getTipoModUsoProcedimento() != null) {
				descricaoItem.append(modoUsoProceds.getTipoModUsoProcedimento()
						.getDescricao());
			}
			if (modoUsoProceds.getQuantidade() != null) {
				descricaoItem.append(' ')
						.append(modoUsoProceds.getQuantidade());
				if (modoUsoProceds.getTipoModUsoProcedimento()
						.getUnidadeMedidaMedica() != null
						&& StringUtils.isNotBlank(modoUsoProceds
								.getTipoModUsoProcedimento()
								.getUnidadeMedidaMedica().getDescricao())) {
					descricaoItem.append(' ')
							.append(modoUsoProceds.getTipoModUsoProcedimento()
									.getUnidadeMedidaMedica().getDescricao());
				}
			} 
			
			descricaoItem = new StringBuffer(StringUtils.capitalize(descricaoItem.toString().toLowerCase().trim()));
			descricaoItem.append(',');
			
			if (!impressaoTotal){
				if (!inclusaoExclusao){
					if (contador > 1){
						descricaoItem.insert(0, CoreUtil.acrescentarEspacos(13));
					}					
				}
			}	
				
			descricaoItem.append(QUEBRA_LINHA);

			descricao.append(descricaoItem);
			contador ++;
		}
		
		if (descricao.length() >= 5){
			return descricao.substring(0, descricao.length() - 5); // remove o último </br>			
		}
		
		return descricao.toString();
	}
	
	
	/**
	 * Método que retorna a descrição indicando o que foi alterado em um
	 * procedimento
	 *
	 * @param procedimento
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String obterDecricaoAlteracaoProcedimento(final Integer atdSeq, final Long seq){
		MpmPrescricaoProcedimento procedimento = mpmPrescricaoProcedimentoDAO.obterPorChavePrimaria(new MpmPrescricaoProcedimentoId(atdSeq, seq));
		StringBuffer descricaoAtualizacao = new StringBuffer();
		
		StringBuffer descricaoDe = null;
		StringBuffer descricaoPara = null;
		
		MpmPrescricaoProcedimento procedimentoAnterior = procedimento.getPrescricaoProcedimento();
		
		if (procedimento.getProcedimentoEspecialDiverso() != null){
			descricaoAtualizacao.append(StringUtils.capitalize(procedimento.getProcedimentoEspecialDiverso().getDescricao().toLowerCase()));	
		}
		
		if (procedimento.getProcedimentoCirurgico() != null){
			descricaoAtualizacao.append(StringUtils.capitalize(procedimento.getProcedimentoCirurgico().getDescricao().toLowerCase()));
		}
		
		if (procedimento.getMatCodigo() != null){
			descricaoAtualizacao.append(' ').append(StringUtils.capitalize(procedimento.getMatCodigo().getNome().toLowerCase()));
		}
		
		String descricaoFilhosAnterior = obterDescricaoFilhosProcedimento(procedimentoAnterior, false, false);
		String descricaoFilhos = obterDescricaoFilhosProcedimento(procedimento, false, false);
		
		if (StringUtils.isNotBlank(descricaoFilhosAnterior)){
			//MUP DE
			descricaoAtualizacao.append(QUEBRA_LINHA).append("DE").append(CoreUtil.acrescentarEspacos(8));
			descricaoAtualizacao.append(descricaoFilhosAnterior);
			
			if (StringUtils.isBlank(descricaoFilhos)){
				descricaoAtualizacao.append(QUEBRA_LINHA).append("PARA").append(CoreUtil.acrescentarEspacos(3));
				descricaoAtualizacao.append("[]");
			}
		}
		
		if (StringUtils.isNotBlank(descricaoFilhos)){
			if (StringUtils.isBlank(descricaoFilhosAnterior)){
				descricaoAtualizacao.append(QUEBRA_LINHA).append("DE").append(CoreUtil.acrescentarEspacos(8));
				descricaoAtualizacao.append("[]");
			}
			//MUP PARA
			descricaoAtualizacao.append(QUEBRA_LINHA).append("PARA").append(CoreUtil.acrescentarEspacos(3));
			descricaoAtualizacao.append(descricaoFilhos);						
		}
		
		if (procedimentoAnterior.getQuantidade() != null
				&& procedimentoAnterior.getQuantidade() > 0) {
			
			descricaoDe = new StringBuffer(procedimentoAnterior.getQuantidade().toString());
			if (procedimentoAnterior.getMatCodigo() != null){
				descricaoDe.append(procedimentoAnterior.getMatCodigo().getUmdCodigo());
			}
		}
		
		if (procedimento.getQuantidade() != null
				&& procedimento.getQuantidade() > 0) {
			
			descricaoPara = new StringBuffer(procedimento.getQuantidade().toString());
			if (procedimento.getMatCodigo() != null){
				descricaoPara.append(procedimento.getMatCodigo().getUmdCodigo());
			}
		}
		
		if (descricaoDe != null || descricaoPara != null){
			if ((descricaoDe != null && descricaoPara == null)
					|| (descricaoDe == null && descricaoPara != null)
					|| (descricaoDe != null && !descricaoDe.toString()
							.equalsIgnoreCase(descricaoPara.toString()))){
			
				if (procedimentoAnterior.getQuantidade() != null
						&& procedimentoAnterior.getQuantidade() > 0){
					
					descricaoAtualizacao.append(", DE ").append(descricaoDe);
				} else{
					descricaoAtualizacao.append(", DE [] ");
				}
				
				if (procedimento.getQuantidade() != null
						&& procedimento.getQuantidade() > 0) {
					
					descricaoAtualizacao.append(_PARA_).append(descricaoPara);
				} else{
					descricaoAtualizacao.append(" PARA [] ");
				}
			} else{
				descricaoAtualizacao.append(' ').append(descricaoPara);
			}
		}
		
		
		//descricaoAtualizacao = descricaoAtualizacao.toLowerCase().trim();
		//descricaoAtualizacao = StringUtils.capitalize(descricaoAtualizacao);
		
		String informacoesComplementares = obterInformacoesComplementaresProcedimento(
				procedimento, procedimentoAnterior);

		//mpmk_sintaxe_rep.mpmc_sint_rep_proc_i (PPR_INF)
		if (StringUtils.isNotBlank(informacoesComplementares)){
			descricaoAtualizacao.append(QUEBRA_LINHA).append(informacoesComplementares);			
		}
		
		return descricaoAtualizacao.toString();
	}
	
	
	/**
	 * mpmk_sintaxe_rep.mpmc_sint_rep_proc_i
	 *
	 * @param procedimento
	 * @param procedimentoAnterior
	 * @return
	 */
	private String obterInformacoesComplementaresProcedimento(
			MpmPrescricaoProcedimento procedimento,
			MpmPrescricaoProcedimento procedimentoAnterior) {
		
		StringBuffer descricao = new StringBuffer();
		
		if (procedimentoAnterior.getInformacaoComplementar() != null || procedimento.getInformacaoComplementar() != null){
			if ((procedimentoAnterior.getInformacaoComplementar() != null && procedimento.getInformacaoComplementar() == null)
					|| (procedimentoAnterior.getInformacaoComplementar() == null && procedimento.getInformacaoComplementar() != null)
					|| (procedimentoAnterior.getInformacaoComplementar() != null && !procedimentoAnterior
							.getInformacaoComplementar().equalsIgnoreCase(procedimento.getInformacaoComplementar()))) {
				
				if (StringUtils.isNotBlank(procedimentoAnterior.getInformacaoComplementar())){
					descricao.append("Inf. Complementares= DE ").append(procedimentoAnterior.getInformacaoComplementar().toUpperCase());
				} else{
					descricao.append("Ind. Complementares= DE []");
				}
				if (StringUtils.isNotBlank(procedimento.getInformacaoComplementar())){
					descricao.append(_PARA_).append(procedimento.getInformacaoComplementar().toUpperCase()).append(',');
				} else{
					descricao.append(" PARA [],");
				}
			} else{
				if (StringUtils.isNotBlank(procedimento.getInformacaoComplementar())){
					descricao.append("Inf. Complementares=").append(procedimento.getInformacaoComplementar().toUpperCase());
				}
			}
		}
		
		
		return descricao.toString();
	}
	


	private String getNumeroFormatado(Number value, String fieldName) {
		String numFormated = "";
		if (value != null) {
			numFormated = AghuNumberFormat.formatarValor(value,
					MpmPrescricaoMdto.class, fieldName);
		}
		return numFormated;
	}
	
	protected MpmItemPrescricaoDietaDAO getItemPrescricaoDietaDAO(){
		return mpmItemPrescricaoDietaDAO;
	}
	
	protected MpmPrescricaoMdtoDAO getPrescricaoMdtoDAO(){
		return mpmPrescricaoMdtoDAO;
	}

	public MpmPrescricaoCuidadoDAO getMpmPrescricaoCuidadoDAO() {
		return mpmPrescricaoCuidadoDAO;
	}

}
