package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioRestricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnf;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidado;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidadoId;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmModeloBasicoDietaId;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamentoId;
import br.gov.mec.aghu.model.MpmModeloBasicoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimentoId;
import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmModoUsoPrescProcedId;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoDietaId;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimentoId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.nutricao.business.INutricaoFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualUnfDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemModeloBasicoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.ItensModeloBasicoVO;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.ItensModeloBasicoVO.Tipo;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EscolherItensModeloBasicoON extends BaseBusiness {

	@EJB
	private ManterDietasModeloBasicoON manterDietasModeloBasicoON;
	
	private static final Log LOG = LogFactory.getLog(EscolherItensModeloBasicoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MpmModeloBasicoCuidadoDAO mpmModeloBasicoCuidadoDAO;
	
	@Inject
	private MpmModeloBasicoMedicamentoDAO mpmModeloBasicoMedicamentoDAO;
	
	@Inject
	private MpmItemModeloBasicoDietaDAO mpmItemModeloBasicoDietaDAO;
	
	@Inject
	private MpmModeloBasicoModoUsoProcedimentoDAO mpmModeloBasicoModoUsoProcedimentoDAO;
	
	@Inject
	private MpmCuidadoUsualUnfDAO mpmCuidadoUsualUnfDAO;
	
	@EJB
	private INutricaoFacade nutricaoFacade;
	
	@Inject
	private MpmModeloBasicoProcedimentoDAO mpmModeloBasicoProcedimentoDAO;
	
	@Inject
	private MpmItemModeloBasicoMedicamentoDAO mpmItemModeloBasicoMedicamentoDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private MpmModeloBasicoDietaDAO mpmModeloBasicoDietaDAO;

	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5376105812202730019L;

	public enum EscolherItensModeloBasicoONExceptionCode implements
			BusinessExceptionCode {
		ERRO_MEDICAMENTO_INATIVO_NA_CARGA, ERRO_SOLUCAO_INATIVA_NA_CARGA, 
		ERRO_CARGA_DIETA_INATIVO, ERRO_CARGA_DIETA_OBRIG_APRAZAMENTO, 
		ERRO_CARGA_DIETA_OBRIG_QUANTIDADE, ERRO_CARGA_DIETA_CAD_APRAZAMENTO, 
		ERRO_CARGA_DIETA_ITEM_UNICO, 
		ERRO_CUIDADO_INATIVO_NA_CARGA,
		ERRO_CARGA_CUIDADO_TIPO_FREQUENCIA_EXIGE_INFORMACAO_FREQUENCIA,
		ERRO_PROCEDIMENTO_INATIVO_NA_CARGA, 
		ERRO_MODELO_BASICO_DIETA_NAO_LOCALIZADO, 
		ERRO_ITENS_MODELO_BASICO_DIETA_NAO_LOCALIZADO,
		MENSAGEM_INFORME_FREQUENCIA_E_TIPO,
		ERRO_CARGA_CUIDADO_TIPO_FREQUENCIA_NAO_PERMITE_INFORMACAO,
		ERRO_CARGA_CUIDADO_NAO_UNIDADE,
		ERRO_CARGA_MEDICAMENTO_DOSE_NAO_PODE_FRACIONADA,
		ERRO_MEDICAMENTO_NAO_AMBULATORIAL_PRESCRICAO
		;
	}

	/**
	 * Percorrer os itens selecionados no VO e proceder com a inclusão
	 * 
	 * @param prescricaoMedica
	 * @param itens
	 * @throws ApplicationBusinessException
	 */
	public Boolean incluirItensSelecionados(
			MpmPrescricaoMedica prescricaoMedica,
			List<ItensModeloBasicoVO> itens, String nomeMicrocomputador) throws BaseException {
		Boolean retorno = false;
		if (itens != null) {
			for (ItensModeloBasicoVO itemModeloBasicoVO : itens) {
				if ((itemModeloBasicoVO.getItemEscolhidoCheckBox() != null)
						&& (itemModeloBasicoVO.getItemEscolhidoCheckBox()
								.booleanValue())) {
					retorno = true;
					incluirItemModeloBasico(prescricaoMedica,
							itemModeloBasicoVO, nomeMicrocomputador);
				}
			}
		}
		return retorno;
	}

	/**
	 * Realizar a chamada dos respectivos métodos conforme o tipo de prescrição
	 * 
	 * @param prescricaoMedica
	 * @param itensModeloBasicoVO
	 * @throws ApplicationBusinessException
	 */
	protected void incluirItemModeloBasico(
			MpmPrescricaoMedica prescricaoMedica,
			ItensModeloBasicoVO itensModeloBasicoVO, String nomeMicrocomputador)
			throws BaseException {

		switch (itensModeloBasicoVO.getTipo()) {
		case DIETA:
			this.validaCargaDieta(prescricaoMedica, itensModeloBasicoVO);
			this.incluirDietaDoModeloBasico(prescricaoMedica,
					itensModeloBasicoVO, nomeMicrocomputador);
			break;
		case CUIDADO:
			this.validaCargaCuidado(prescricaoMedica, itensModeloBasicoVO);
			incluirCuidadoDoModeloBasico(prescricaoMedica, itensModeloBasicoVO, nomeMicrocomputador);
			break;
		case MEDICAMENTO:
		case SOLUCAO:
			this.validaCargaMedicamento(prescricaoMedica, itensModeloBasicoVO);
			incluirMedicamentoDoModeloBasico(prescricaoMedica,
					itensModeloBasicoVO, nomeMicrocomputador);
			break;
		case PROCEDIMENTO:
			this.validaCargaProcedimento(prescricaoMedica, itensModeloBasicoVO);
			incluirProcedimentoDoModeloBasico(prescricaoMedica,
					itensModeloBasicoVO, nomeMicrocomputador);
			break;
		default:
			break;
		}
	}

	protected void incluirProcedimentoDoModeloBasico(
			MpmPrescricaoMedica prescricaoMedica,
			ItensModeloBasicoVO itensModeloBasicoVO, String nomeMicrocomputador)
			throws BaseException {		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			MpmPrescricaoProcedimento procedimento = new MpmPrescricaoProcedimento();
			procedimento.setId(new MpmPrescricaoProcedimentoId(prescricaoMedica
					.getAtendimento().getSeq(), null));

			
			// this.getMpmPrescricaoProcedimentoDAO().obterValorSequencialId(
			// procedimento);

			MpmModeloBasicoProcedimento modeloBasicoProcedimento = this
					.getMpmModeloBasicoProcedimentoDAO().obterPorChavePrimaria(
							new MpmModeloBasicoProcedimentoId(
									itensModeloBasicoVO
											.getModeloBasicoPrescricaoSeq(),
									itensModeloBasicoVO.getItemSeq()
											.shortValue()));


			procedimento.setPrescricaoMedica(prescricaoMedica);
			
			procedimento.getPrescricaoMedica().setAtendimento(prescricaoMedica.getAtendimento());
			procedimento.setServidor(servidorLogado);

			procedimento.setProcedimentoCirurgico(modeloBasicoProcedimento
					.getProcedimentoCirurgico());
			procedimento.setMatCodigo(modeloBasicoProcedimento.getMaterial());
			procedimento.setProcedimentoEspecialDiverso(modeloBasicoProcedimento
					.getProcedEspecialDiverso());

			// data de incício depende se a prescrição está vigente.
			if (prescricaoMedica.isPrescricaoMedicaFutura()) {

				// se prescrição vigente.
				procedimento.setDthrInicio((Date) prescricaoMedica
						.getDthrMovimento().clone());
			} else {

				// se prescrição futura.
				procedimento.setDthrInicio((Date) prescricaoMedica
						.getDthrInicio().clone());
			}

			procedimento.setDthrFim((Date) prescricaoMedica.getDthrFim()
					.clone());
			procedimento.setCriadoEm(Calendar.getInstance().getTime());
			procedimento.setIndPendente(DominioIndPendenteItemPrescricao.B);
			procedimento
					.setQuantidade(modeloBasicoProcedimento.getQuantidade());
			procedimento.setInformacaoComplementar(modeloBasicoProcedimento
					.getInformacoesComplementares());
			procedimento.setDigitacaoSolicitante(true);
			procedimento.setPrescricaoMedica(prescricaoMedica);

			List<MpmModeloBasicoModoUsoProcedimento> modeloBasicoModoUsoProced = this
					.getMpmModeloBasicoModoUsoProcedimentoDAO().pesquisar(
							modeloBasicoProcedimento);

			List<MpmModoUsoPrescProced> modoUsoPrescProced = new ArrayList<MpmModoUsoPrescProced>();

			for (int i = 0; i < modeloBasicoModoUsoProced.size(); ++i) {

				MpmModoUsoPrescProced itemPrescProced = new MpmModoUsoPrescProced();

				itemPrescProced.setPrescricaoProcedimento(procedimento);
				itemPrescProced.setId(new MpmModoUsoPrescProcedId(
						prescricaoMedica.getAtendimento().getSeq(),
						procedimento.getId().getSeq(), i + 1));
				itemPrescProced
						.setTipoModUsoProcedimento(modeloBasicoModoUsoProced
								.get(i).getTipoModoUsoProcedimento());
				itemPrescProced.setQuantidade(modeloBasicoModoUsoProced.get(i)
						.getQuantidade());

				itemPrescProced.setPrescricaoProcedimento(procedimento);

				modoUsoPrescProced.add(itemPrescProced);
			}

			procedimento.setModoUsoPrescricaoProcedimentos(modoUsoPrescProced);

			this.getPrescricaoMedicaFacade().inserirCargaPrescricaoProcedimentoEspecial(procedimento, prescricaoMedica, nomeMicrocomputador);
	}

	protected void incluirMedicamentoDoModeloBasico(
			MpmPrescricaoMedica prescricaoMedica,
			ItensModeloBasicoVO itensModeloBasicoVO, String nomeMicrocomputador)
			throws BaseException {
		MpmModeloBasicoMedicamentoId modeloBasicoMedicamentoId = new MpmModeloBasicoMedicamentoId(
				itensModeloBasicoVO.getModeloBasicoPrescricaoSeq(),
				itensModeloBasicoVO.getItemSeq());
		MpmModeloBasicoMedicamento modeloBasicoMedicamento = getMpmModeloBasicoMedicamentoDAO()
				.obterPorChavePrimaria(modeloBasicoMedicamentoId);
		MpmPrescricaoMdto mpmPrescricaoMedicamento = montarMpmPrescricaoMdtoInsercao(
				prescricaoMedica, modeloBasicoMedicamento);
		// lista de itens do medicamento
		List<MpmItemPrescricaoMdto> itensPrescricaoMedicamento = new ArrayList<MpmItemPrescricaoMdto>();
		List<MpmItemModeloBasicoMedicamento> itensModeloBasicoMedicamento = getMpmItemModeloBasicoMedicamentoDAO()
				.obterItensMedicamento(
						itensModeloBasicoVO.getModeloBasicoPrescricaoSeq(),
						modeloBasicoMedicamento.getId().getSeq());
		for (MpmItemModeloBasicoMedicamento mpmItemModeloBasicoMedicamento : itensModeloBasicoMedicamento) {
			MpmItemPrescricaoMdto mpmItemPrescricaoMdto = montarMpmItemPrescricaoMdto(
					prescricaoMedica, mpmItemModeloBasicoMedicamento);
			mpmItemPrescricaoMdto
					.setPrescricaoMedicamento(mpmPrescricaoMedicamento);
			itensPrescricaoMedicamento.add(mpmItemPrescricaoMdto);

		}
		mpmPrescricaoMedicamento
				.setItensPrescricaoMdtos(itensPrescricaoMedicamento);
		// insere a prescricao de medicamento e os respectivos itens
		getPrescricaoMedicaFacade()
				.inserirPrescricaoMedicamentoModeloBasico(
						mpmPrescricaoMedicamento, nomeMicrocomputador);
	}

	private MpmItemPrescricaoMdto montarMpmItemPrescricaoMdto(
			MpmPrescricaoMedica prescricaoMedica,
			MpmItemModeloBasicoMedicamento mpmItemModeloBasicoMedicamento) throws ApplicationBusinessException {
		MpmItemPrescricaoMdto mpmItemPrescricaoMdto = new MpmItemPrescricaoMdto();
		MpmItemPrescricaoMdtoId id = new MpmItemPrescricaoMdtoId();
		id.setPmdAtdSeq(prescricaoMedica.getAtendimento().getSeq());
		// id.setPmdSeq(prescricaoMedica.getId().getSeq().longValue());
		id.setMedMatCodigo(mpmItemModeloBasicoMedicamento.getId()
				.getMedicamentoMaterialCodigo());
		mpmItemPrescricaoMdto.setId(id);
		mpmItemPrescricaoMdto.setFormaDosagem(mpmItemModeloBasicoMedicamento
				.getFormaDosagem());
		mpmItemPrescricaoMdto.setDose(mpmItemModeloBasicoMedicamento.getDose());
		mpmItemPrescricaoMdto.setMdtoAguardaEntrega(Boolean.FALSE);
		mpmItemPrescricaoMdto.setQtdeCalcSist24h(getPrescricaoMedicaFacade()
				.buscaCalculoQuantidade24Horas(
						mpmItemModeloBasicoMedicamento.getModeloBasicoMedicamento() != null ? mpmItemModeloBasicoMedicamento.getModeloBasicoMedicamento().getFrequencia(): null,
								mpmItemModeloBasicoMedicamento.getModeloBasicoMedicamento().getTipoFrequenciaAprazamento() != null ? mpmItemModeloBasicoMedicamento.getModeloBasicoMedicamento().getTipoFrequenciaAprazamento().getSeq(): null,
						mpmItemModeloBasicoMedicamento.getDose(),
						mpmItemModeloBasicoMedicamento.getFormaDosagem().getSeq(),
						mpmItemModeloBasicoMedicamento.getMedicamento() != null ? mpmItemModeloBasicoMedicamento.getMedicamento().getMatCodigo() : null));
		mpmItemPrescricaoMdto.setObservacao(mpmItemModeloBasicoMedicamento
				.getObservacao());
		mpmItemPrescricaoMdto.setMedicamento(mpmItemModeloBasicoMedicamento
				.getMedicamento());
		return mpmItemPrescricaoMdto;
	}

	private MpmPrescricaoMdto montarMpmPrescricaoMdtoInsercao(
			MpmPrescricaoMedica prescricaoMedica,
			MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		MpmPrescricaoMdto mpmPrescricaoMedicamento = new MpmPrescricaoMdto();
		MpmPrescricaoMdtoId mpmPrescricaoMdtoId = new MpmPrescricaoMdtoId(
				prescricaoMedica.getAtendimento().getSeq(), null);
		mpmPrescricaoMedicamento.setId(mpmPrescricaoMdtoId);
		

		mpmPrescricaoMedicamento.setPrescricaoMedica(prescricaoMedica);
		
		mpmPrescricaoMedicamento.getPrescricaoMedica().setAtendimento(prescricaoMedica
				.getAtendimento());
		mpmPrescricaoMedicamento
				.setTipoVelocAdministracao(modeloBasicoMedicamento
						.getTipoVelocidadeAdministracao());
		mpmPrescricaoMedicamento.setTipoFreqAprazamento(modeloBasicoMedicamento
				.getTipoFrequenciaAprazamento());
		mpmPrescricaoMedicamento.setServidor(servidorLogado);
		mpmPrescricaoMedicamento.setViaAdministracao(modeloBasicoMedicamento
				.getViaAdministracao());
		// prescrição vigente ou futura
		if (prescricaoMedica.isPrescricaoMedicaVigente()) {
			mpmPrescricaoMedicamento.setDthrInicio(new Date(prescricaoMedica
					.getDthrMovimento().getTime()));
		} else if (prescricaoMedica.isPrescricaoMedicaFutura()) {
			mpmPrescricaoMedicamento.setDthrInicio(new Date(prescricaoMedica
					.getDthrInicio().getTime()));
		}
		if (prescricaoMedica.getDthrFim() != null) {
			mpmPrescricaoMedicamento.setDthrFim(new Date(prescricaoMedica
					.getDthrFim().getTime()));
		}
		mpmPrescricaoMedicamento.setCriadoEm(new Date());
		mpmPrescricaoMedicamento.setIndSeNecessario(modeloBasicoMedicamento
				.getIndSeNecessario());
		/*
		 * Verifica se a unidade funcional onde o paciente está internado
		 * (agh_atendimentos.unf_seq) tem característica 'Permite Prescrição BI'
		 * ,
		 */
		Boolean indBombaInfusao = Boolean.FALSE;
		if (getPrescricaoMedicaFacade().isUnidadeBombaInfusao(
				prescricaoMedica.getAtendimento().getUnidadeFuncional())
				.booleanValue()) {
			//Verifica se o modelo básico possui o IND_BOMBA_INFUSAO marcado
			if(modeloBasicoMedicamento.getIndBombaInfusao()) {
				indBombaInfusao = true;
			} else {
				for (MpmItemModeloBasicoMedicamento item : modeloBasicoMedicamento
						.getItensModeloMedicamento()) {
					AfaViaAdministracaoMedicamentoId afaId = new AfaViaAdministracaoMedicamentoId(
							item.getId().getMedicamentoMaterialCodigo(),
							modeloBasicoMedicamento.getViaAdministracao()
									.getSigla());
					AfaViaAdministracaoMedicamento afaViaAdmMedicamento = getFarmaciaFacade().obterViaAdministracaoMedicamento(afaId);
					if (afaViaAdmMedicamento != null){
						if (DominioSituacao.A
								.equals(afaViaAdmMedicamento.getSituacao())
								&& afaViaAdmMedicamento.getDefaultBi()) {
							indBombaInfusao = Boolean.TRUE;
							break;
						}					
					}
				}
			}
		}
		mpmPrescricaoMedicamento.setIndBombaInfusao(indBombaInfusao);
		mpmPrescricaoMedicamento.setIndItemRecomendadoAlta(false);
		mpmPrescricaoMedicamento
				.setIndPendente(DominioIndPendenteItemPrescricao.B);
		mpmPrescricaoMedicamento.setFrequencia(modeloBasicoMedicamento
				.getFrequencia());
		if (modeloBasicoMedicamento.getHoraInicioAdministracao() != null) {
			mpmPrescricaoMedicamento.setHoraInicioAdministracao(new Date(
					modeloBasicoMedicamento.getHoraInicioAdministracao()
							.getTime()));
		}
		mpmPrescricaoMedicamento.setObservacao(modeloBasicoMedicamento
				.getObservacao());
		mpmPrescricaoMedicamento.setGotejo(modeloBasicoMedicamento.getGotejo());
		if (modeloBasicoMedicamento.getQuantidadeHorasCorrer() != null) {
			mpmPrescricaoMedicamento.setQtdeHorasCorrer(modeloBasicoMedicamento
					.getQuantidadeHorasCorrer().shortValue());
			mpmPrescricaoMedicamento.setUnidHorasCorrer(modeloBasicoMedicamento.getUnidHorasCorrer());
		}
		mpmPrescricaoMedicamento.setIndSolucao(modeloBasicoMedicamento
				.getIndSolucao());
		mpmPrescricaoMedicamento.setVolumeDiluenteMl(modeloBasicoMedicamento.getVolumeDiluenteMl());
		mpmPrescricaoMedicamento.setDiluente(modeloBasicoMedicamento.getDiluente());
		mpmPrescricaoMedicamento.setPrescricaoMedica(prescricaoMedica);
		return mpmPrescricaoMedicamento;
	}

	/**
	 * Incluir Prescricao de Cuidado à partir do modelo básico
	 * 
	 * @param prescricaoMedica
	 * @param itensModeloBasicoVO
	 * @throws ApplicationBusinessException
	 */
	protected void incluirCuidadoDoModeloBasico(
			MpmPrescricaoMedica prescricaoMedica,
			ItensModeloBasicoVO itensModeloBasicoVO, String nomeMicrocomputador)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		MpmModeloBasicoCuidadoId id = new MpmModeloBasicoCuidadoId(
				itensModeloBasicoVO.getModeloBasicoPrescricaoSeq(),
				itensModeloBasicoVO.getItemSeq());
		MpmModeloBasicoCuidado modeloBasicoCuidado = getMpmModeloBasicoCuidadoDAO()
				.obterPorChavePrimaria(id);
		MpmPrescricaoCuidado prescricaoCuidado = new MpmPrescricaoCuidado();

		prescricaoCuidado.setIndPendente(DominioIndPendenteItemPrescricao.B);
		prescricaoCuidado.setPrescricaoMedica(prescricaoMedica);
		prescricaoCuidado.setMpmTipoFreqAprazamentos(modeloBasicoCuidado
				.getTipoFrequenciaAprazamento());
		prescricaoCuidado.setMpmCuidadoUsuais(modeloBasicoCuidado
				.getCuidadoUsual());
		prescricaoCuidado.setFrequencia(modeloBasicoCuidado.getFrequencia());
		prescricaoCuidado.setDescricao(modeloBasicoCuidado.getDescricao());
		prescricaoCuidado.setServidor(servidorLogado);

		// insere a prescricao de Cuidado
		getPrescricaoMedicaFacade().incluirPrescricaoCuidado(prescricaoCuidado, nomeMicrocomputador, new Date());

	}

	private MpmItemModeloBasicoMedicamentoDAO getMpmItemModeloBasicoMedicamentoDAO() {
		return mpmItemModeloBasicoMedicamentoDAO;
	}
	
	/**
	 * Pré-validação - Valida se item possui medicamento Inativo
	 */
	private void validaCargaMedicamento(MpmPrescricaoMedica prescricaoMedica,
			ItensModeloBasicoVO itensModeloBasicoVO)
			throws ApplicationBusinessException {

		MpmModeloBasicoMedicamento modeloBasicoMedicamento = getMpmModeloBasicoMedicamentoDAO()
				.obterModeloBasicoMedicamento(
						itensModeloBasicoVO.getModeloBasicoPrescricaoSeq(),
						itensModeloBasicoVO.getItemSeq());

		for (MpmItemModeloBasicoMedicamento itensModeloMedicamento : modeloBasicoMedicamento
				.getItensModeloMedicamento()) {

			if (itensModeloMedicamento.getMedicamento() != null) {
				AghAtendimentos atd = getAghuFacade().obterAtendimento(
						prescricaoMedica.getId().getAtdSeq(),
						null,DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial());
				//Gap #34801
				if(atd != null){
					Long countMdto = getFarmaciaFacade().obterMedicamentosVOCount(itensModeloMedicamento.getMedicamento().getMatCodigo().toString(), null, null, Boolean.TRUE, Boolean.FALSE);
					if(countMdto == 0){
						itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
						throw new ApplicationBusinessException(
								EscolherItensModeloBasicoONExceptionCode.ERRO_MEDICAMENTO_NAO_AMBULATORIAL_PRESCRICAO
								,itensModeloBasicoVO.getDescricao());
					}
				}
				if (itensModeloBasicoVO.getTipo().equals(Tipo.MEDICAMENTO)) {
					if (!DominioSituacaoMedicamento.A
							.equals(itensModeloMedicamento.getMedicamento()
									.getIndSituacao())) {
						itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
						throw new ApplicationBusinessException(
								EscolherItensModeloBasicoONExceptionCode.ERRO_MEDICAMENTO_INATIVO_NA_CARGA,
								itensModeloBasicoVO.getDescricao());
					}
				}
				if (itensModeloBasicoVO.getTipo().equals(Tipo.SOLUCAO)) {
					if (!DominioSituacaoMedicamento.A
							.equals(itensModeloMedicamento.getMedicamento()
									.getIndSituacao())) {
						itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
						throw new ApplicationBusinessException(
								EscolherItensModeloBasicoONExceptionCode.ERRO_SOLUCAO_INATIVA_NA_CARGA,
								itensModeloBasicoVO.getDescricao());
					}
				}
			}
		
			// verifica se a dose não pode ser fracionada
			if (!itensModeloMedicamento.getMedicamento()
					.getIndPermiteDoseFracionada()) {
				// mpm_item_mod_basico_mdtos.dose !=
				// trunc(mpm_item_mod_basico_mdtos.dose),
				if (!itensModeloMedicamento.getDose().equals(
						itensModeloMedicamento.getDose().setScale(0,
								RoundingMode.DOWN))) {
					itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
					throw new ApplicationBusinessException(
							EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_MEDICAMENTO_DOSE_NAO_PODE_FRACIONADA,itensModeloBasicoVO.getDescricao());

				}
			}
			if (!itensModeloMedicamento.getMedicamento().getIndPermiteDoseFracionada()
					&& itensModeloMedicamento.getDose() != null && !(itensModeloMedicamento.getDose().stripTrailingZeros().scale() <= 0)) {
				itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
				throw new ApplicationBusinessException(
						EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_MEDICAMENTO_DOSE_NAO_PODE_FRACIONADA,itensModeloBasicoVO.getDescricao());
			}

			
		}
	}

	/**
	 * Pré-Validação carga de modelo básico de Dieta.
	 * 
	 * @param prescricaoMedica
	 * @param itensModeloBasicoVO
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void validaCargaDieta(MpmPrescricaoMedica prescricaoMedica,
			ItensModeloBasicoVO itensModeloBasicoVO)
			throws ApplicationBusinessException {

		MpmModeloBasicoDietaId modeloBasicoDietaId = new MpmModeloBasicoDietaId(
				itensModeloBasicoVO.getModeloBasicoPrescricaoSeq(),
				itensModeloBasicoVO.getItemSeq());
		MpmModeloBasicoDieta modeloBasicodieta = getMpmModeloBasicoDietaDAO()
				.obterPorChavePrimaria(modeloBasicoDietaId);

		for (MpmItemModeloBasicoDieta itemDieta : modeloBasicodieta.getItens()) {

			AnuTipoItemDieta anuTipoItemDieta = this.getNutricaoFacade()
					.obterAnuTipoItemDietaPorChavePrimaria(
							itemDieta.getTipoItemDieta().getSeq());

			// verifica se tipo item dieta existe e está ativo
			if (anuTipoItemDieta == null
					|| DominioSituacao.I.equals(anuTipoItemDieta
							.getIndSituacao())) {
				itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
				throw new ApplicationBusinessException(
						EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_DIETA_INATIVO,
						itensModeloBasicoVO.getDescricao());
			}

			// Valida Obrigatoriedade de Aprazamento
			if (DominioRestricao.O.equals(itemDieta.getTipoItemDieta()
					.getIndDigitaAprazamento())) {
				if (itemDieta.getTipoFrequenciaAprazamento() == null) {
					itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
					throw new ApplicationBusinessException(
							EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_DIETA_OBRIG_APRAZAMENTO,
							itensModeloBasicoVO.getDescricao());
				}
			} else {
				if (DominioRestricao.N.equals(itemDieta.getTipoItemDieta()
						.getIndDigitaAprazamento())) {
					if (itemDieta.getTipoFrequenciaAprazamento() != null) {
						itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
						throw new ApplicationBusinessException(
								EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_DIETA_OBRIG_APRAZAMENTO,
								itensModeloBasicoVO.getDescricao());
					}
				}
			}

			// Validação de Quantidade
			if (DominioRestricao.O.equals(itemDieta.getTipoItemDieta()
					.getIndDigitaQuantidade())) {
				if (itemDieta.getQuantidade() == null
						|| itemDieta.getQuantidade().equals(BigDecimal.ZERO)) {
					itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
					throw new ApplicationBusinessException(
							EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_DIETA_OBRIG_QUANTIDADE,
							itensModeloBasicoVO.getDescricao());
				}
			} else {
				if (DominioRestricao.N.equals(itemDieta.getTipoItemDieta()
						.getIndDigitaQuantidade())) {
					if (itemDieta.getQuantidade() != null) {
						itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
						throw new ApplicationBusinessException(
								EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_DIETA_OBRIG_QUANTIDADE,
								itensModeloBasicoVO.getDescricao());
					}
				}
			}

			// valida frequencia no item e anuTipoItemDieta
			if (anuTipoItemDieta.getFrequencia() != null) {
				if (anuTipoItemDieta.getFrequencia() != itemDieta
						.getFrequencia()) {
					itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
					throw new ApplicationBusinessException(
							EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_DIETA_CAD_APRAZAMENTO,
							itensModeloBasicoVO.getDescricao());
				}
			}

			// valida tipoFrequenciaAprezamento no item e anuTipoItemDieta
			if (anuTipoItemDieta.getTipoFrequenciaAprazamento() != null) {
				if (anuTipoItemDieta.getTipoFrequenciaAprazamento() != itemDieta
						.getTipoFrequenciaAprazamento()) {
					itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
					throw new ApplicationBusinessException(
							EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_DIETA_CAD_APRAZAMENTO,
							itensModeloBasicoVO.getDescricao());
				}
			}

			// se este item possuir indicador de item único = S e ja existe
			// outro
			if (itemDieta.getTipoItemDieta().getIndItemUnico()) {
				Long vcont = 0l;

				vcont = this.getMpmItemModeloBasicoDietaDAO()
						.countItemModeloBasicoDieta(
								itemDieta.getModeloBasicoDieta()
										.getModeloBasicoPrescricao().getSeq(),
								itemDieta.getModeloBasicoDieta().getId()
										.getSeq());
				if (vcont > 0) {
					itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
					throw new ApplicationBusinessException(
							EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_DIETA_ITEM_UNICO,
							itensModeloBasicoVO.getDescricao());
				}
			}
			// se ja existe no banco um item com indicador de unico
			for (MpmItemModeloBasicoDieta itemExistente : this
					.getMpmItemModeloBasicoDietaDAO().pesquisar(
							itemDieta.getModeloBasicoDieta()
									.getModeloBasicoPrescricao().getSeq(),
							itemDieta.getModeloBasicoDieta().getId().getSeq())) {
				if (itemExistente.getTipoItemDieta().getIndItemUnico()) {
					itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
					throw new ApplicationBusinessException(
							EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_DIETA_ITEM_UNICO,
							itensModeloBasicoVO.getDescricao());
				}
			}

		}

	}

	/**
	 * Pré validação - Valida se item possui Cuidado Inativo
	 */
	private void validaCargaCuidado(MpmPrescricaoMedica prescricaoMedica,
			ItensModeloBasicoVO itensModeloBasicoVO)
			throws ApplicationBusinessException {

		MpmModeloBasicoCuidadoId id = new MpmModeloBasicoCuidadoId(
				itensModeloBasicoVO.getModeloBasicoPrescricaoSeq(),
				itensModeloBasicoVO.getItemSeq());
		MpmModeloBasicoCuidado modeloBasicoCuidado = getMpmModeloBasicoCuidadoDAO()
				.obterPorChavePrimaria(id);

		if (!DominioSituacao.A.equals(modeloBasicoCuidado.getCuidadoUsual()
				.getIndSituacao())) {
			itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
			throw new ApplicationBusinessException(
					EscolherItensModeloBasicoONExceptionCode.ERRO_CUIDADO_INATIVO_NA_CARGA,
					itensModeloBasicoVO.getDescricao());
		}

		//Valida tipo de frequência
		if (modeloBasicoCuidado.getFrequencia() != null
				&& modeloBasicoCuidado.getTipoFrequenciaAprazamento() == null) {
			itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
			throw new ApplicationBusinessException(
					EscolherItensModeloBasicoONExceptionCode.MENSAGEM_INFORME_FREQUENCIA_E_TIPO);
		}

		if (modeloBasicoCuidado.getTipoFrequenciaAprazamento() != null) {
			if (modeloBasicoCuidado.getTipoFrequenciaAprazamento()
					.getIndDigitaFrequencia()
					&& (modeloBasicoCuidado.getFrequencia() == null || modeloBasicoCuidado
							.getFrequencia().equals(0))) {
				itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
				throw new ApplicationBusinessException(
						EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_CUIDADO_TIPO_FREQUENCIA_EXIGE_INFORMACAO_FREQUENCIA,itensModeloBasicoVO.getDescricao() );
			}

			if (!modeloBasicoCuidado.getTipoFrequenciaAprazamento()
					.getIndDigitaFrequencia()
					&& modeloBasicoCuidado.getFrequencia() != null) {
				itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
				throw new ApplicationBusinessException(
						EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_CUIDADO_TIPO_FREQUENCIA_NAO_PERMITE_INFORMACAO,itensModeloBasicoVO.getDescricao());
			}
		}
		
		// se cuidado é permitido para a unidade
		if (modeloBasicoCuidado.getCuidadoUsual() != null
				&& prescricaoMedica.getAtendimento() != null
				&& prescricaoMedica.getAtendimento()
						.getUnidadeFuncional() != null) {

			MpmCuidadoUsualUnf cuidadoUnf = this.getMpmCuidadoUsualUnfDAO()
					.obterPorChavePrimaria(
							modeloBasicoCuidado.getCuidadoUsual().getSeq(),
							prescricaoMedica.getAtendimento()
							.getUnidadeFuncional()
									.getSeq());

			if (cuidadoUnf == null) {
				itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
				throw new ApplicationBusinessException(
						EscolherItensModeloBasicoONExceptionCode.ERRO_CARGA_CUIDADO_NAO_UNIDADE,
						itensModeloBasicoVO.getDescricao());
			}
		}
		

	}

	/**
	 * Pré validação - Valida se item possui Procedimento Inativo
	 */
	private void validaCargaProcedimento(MpmPrescricaoMedica prescricaoMedica,
			ItensModeloBasicoVO itensModeloBasicoVO)
			throws ApplicationBusinessException {

		MpmModeloBasicoProcedimentoId id = new MpmModeloBasicoProcedimentoId(
				itensModeloBasicoVO.getModeloBasicoPrescricaoSeq(),
				itensModeloBasicoVO.getItemSeq().shortValue());
		MpmModeloBasicoProcedimento modeloBasicoProcedimento = getMpmModeloBasicoProcedimentoDAO()
				.obterPorChavePrimaria(id);

		if ((modeloBasicoProcedimento.getMaterial() != null)
				&& (modeloBasicoProcedimento.getMaterial().getIndSituacao() != null)) {
			if (!DominioSituacao.A.equals(modeloBasicoProcedimento
					.getMaterial().getIndSituacao())) {
				itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
				throw new ApplicationBusinessException(
						EscolherItensModeloBasicoONExceptionCode.ERRO_PROCEDIMENTO_INATIVO_NA_CARGA,
						itensModeloBasicoVO.getDescricao());
			}
		}

		if ((modeloBasicoProcedimento.getProcedimentoCirurgico() != null)
				&& (modeloBasicoProcedimento.getProcedimentoCirurgico()
						.getIndSituacao() != null)) {
			if (!DominioSituacao.A.equals(modeloBasicoProcedimento
					.getProcedimentoCirurgico().getIndSituacao())) {
				itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
				throw new ApplicationBusinessException(
						EscolherItensModeloBasicoONExceptionCode.ERRO_PROCEDIMENTO_INATIVO_NA_CARGA,
						itensModeloBasicoVO.getDescricao());
			}
		}

		if ((modeloBasicoProcedimento.getProcedEspecialDiverso() != null)
				&& (modeloBasicoProcedimento.getProcedEspecialDiverso()
						.getIndSituacao() != null)) {
			if (!DominioSituacao.A.equals(modeloBasicoProcedimento
					.getProcedEspecialDiverso().getIndSituacao())) {
				itensModeloBasicoVO.setItemEscolhidoCheckBox(false);
				throw new ApplicationBusinessException(
						EscolherItensModeloBasicoONExceptionCode.ERRO_PROCEDIMENTO_INATIVO_NA_CARGA,
						itensModeloBasicoVO.getDescricao());
			}
		}

	}
	

	/**
	 * Realizar a inclusão da prescrição de dieta e seus respectivos itens a
	 * partir do modelo básico
	 * 
	 * @param prescricaoMedica
	 * @param modeloBasicoVO
	 * @throws ApplicationBusinessException
	 */
	protected void incluirDietaDoModeloBasico(
			MpmPrescricaoMedica prescricaoMedica,
			ItensModeloBasicoVO modeloBasicoVO, String nomeMicrocomputador) throws BaseException {

		if (prescricaoMedica == null || modeloBasicoVO == null) {
			throw new IllegalArgumentException("Parâmetros obrigatórios");
		}

		MpmModeloBasicoDieta modeloBasicoDieta = 
		this.getManterDietasModeloBasicoON().obterModeloBasicoDieta(
				 modeloBasicoVO.getModeloBasicoPrescricaoSeq(),
				 modeloBasicoVO.getItemSeq()
		);
		
		if (modeloBasicoDieta == null) {
			throw new ApplicationBusinessException(
					EscolherItensModeloBasicoONExceptionCode.ERRO_MODELO_BASICO_DIETA_NAO_LOCALIZADO);
		}

		MpmPrescricaoDieta prescricaoDieta = this
				.montarPrescricaoDietaDoModeloBasico(prescricaoMedica,
						modeloBasicoDieta);

		this.getPrescricaoMedicaFacade().validarInserirDieta(prescricaoDieta);
		this.getPrescricaoMedicaFacade()
				.inserirPrescricaoDieta(prescricaoDieta, nomeMicrocomputador);
		this.incluirItensPrescricaoDietaDoModeloBasico(modeloBasicoDieta,
				prescricaoDieta);
	}

	/**
	 * Montar a prescrição de dieta a partir do modelo básico para posterior
	 * inclusão na prescrição médica
	 * 
	 * @param prescricaoMedica
	 * @param modeloBasicoDieta
	 */
	protected MpmPrescricaoDieta montarPrescricaoDietaDoModeloBasico(
			MpmPrescricaoMedica prescricaoMedica,
			MpmModeloBasicoDieta modeloBasicoDieta) {

		MpmPrescricaoDieta prescricaoDieta = new MpmPrescricaoDieta(
				new MpmPrescricaoDietaId(prescricaoMedica.getId().getAtdSeq(),
						null));

		prescricaoDieta.setPrescricaoMedica(prescricaoMedica);
		prescricaoDieta.setIndPendente(DominioIndPendenteItemPrescricao.B);
		prescricaoDieta.setIndItemRecomendadoAlta(false);
		prescricaoDieta.setCriadoEm(Calendar.getInstance().getTime());
		prescricaoDieta.setIndAvalNutricionista(modeloBasicoDieta
				.getIndAvalNutricionista());
		prescricaoDieta.setIndItemRecTransferencia(false);
		prescricaoDieta.setObservacao(modeloBasicoDieta.getObservacao());
		if (this.getPrescricaoMedicaFacade().isPrescricaoVigente(
				prescricaoMedica)) {
			prescricaoDieta.setDthrInicio(prescricaoMedica.getDthrMovimento());
		} else {
			prescricaoDieta.setDthrInicio(prescricaoMedica.getDthrInicio());
		}
		prescricaoDieta.setDthrFim(prescricaoMedica.getDthrFim());

		//Verifica se a unidade funcional onde o paciente está internado tem característica 'Permite Prescrição BI'
		boolean indBombaInfusao = false;
		if(getPrescricaoMedicaFacade().isUnidadeBombaInfusao(prescricaoMedica.getAtendimento().getUnidadeFuncional())) {
			//Verifica se o modelo básico possui o IND_BOMBA_INFUSAO marcado
			if(modeloBasicoDieta.getIndBombaInfusao()) {
				indBombaInfusao = true;
			}
		}
		prescricaoDieta.setIndBombaInfusao(indBombaInfusao);
		
		return prescricaoDieta;
	}
	
	private ManterDietasModeloBasicoON getManterDietasModeloBasicoON() {
		return manterDietasModeloBasicoON;
	}

	/**
	 * 
	 * Realizar a inclusão dos itens da prescrição de dieta a partir do modelo
	 * básico
	 * 
	 * @param modeloBasicoDieta
	 * @param prescricaoDieta
	 * @throws ApplicationBusinessException
	 */
	protected void incluirItensPrescricaoDietaDoModeloBasico(
			MpmModeloBasicoDieta modeloBasicoDieta,
			MpmPrescricaoDieta prescricaoDieta) throws BaseException {

		List<MpmItemModeloBasicoDieta> itensModeloBasicoDieta = this.getManterDietasModeloBasicoON().obterListaItensDieta(
				modeloBasicoDieta.getId().getModeloBasicoPrescricaoSeq(),
				modeloBasicoDieta.getId().getSeq()
		);

		if (itensModeloBasicoDieta == null) {
			throw new ApplicationBusinessException(
					EscolherItensModeloBasicoONExceptionCode.ERRO_ITENS_MODELO_BASICO_DIETA_NAO_LOCALIZADO);
		}

		List<MpmItemPrescricaoDieta> itensPrescricaoDieta = new ArrayList<MpmItemPrescricaoDieta>();

		for (MpmItemModeloBasicoDieta item : itensModeloBasicoDieta) {

			MpmItemPrescricaoDieta itemPrescricaoDieta = new MpmItemPrescricaoDieta();
			itemPrescricaoDieta.setTipoItemDieta(item.getTipoItemDieta());
			itemPrescricaoDieta.setTipoFreqAprazamento(item
					.getTipoFrequenciaAprazamento());
			itemPrescricaoDieta.setQuantidade(item.getQuantidade());
			itemPrescricaoDieta.setFrequencia(item.getFrequencia());
			itemPrescricaoDieta.setDuracaoSolicitada(null);
			itemPrescricaoDieta.setNumVezes(item.getNumeroVezes());
			itemPrescricaoDieta.setPrescricaoDieta(prescricaoDieta);

			itensPrescricaoDieta.add(itemPrescricaoDieta);
		}

		this.getPrescricaoMedicaFacade().inserir(prescricaoDieta,
				itensPrescricaoDieta);
	}

	private MpmModeloBasicoMedicamentoDAO getMpmModeloBasicoMedicamentoDAO() {
		return mpmModeloBasicoMedicamentoDAO;
	}

	protected MpmModeloBasicoDietaDAO getMpmModeloBasicoDietaDAO() {
		return mpmModeloBasicoDietaDAO;
	}

	protected MpmModeloBasicoCuidadoDAO getMpmModeloBasicoCuidadoDAO() {
		return mpmModeloBasicoCuidadoDAO;
	}
	
	protected MpmCuidadoUsualUnfDAO getMpmCuidadoUsualUnfDAO() {
		return mpmCuidadoUsualUnfDAO;
	}

	protected MpmModeloBasicoProcedimentoDAO getMpmModeloBasicoProcedimentoDAO() {
		return mpmModeloBasicoProcedimentoDAO;
	}

	protected INutricaoFacade getNutricaoFacade() {
		return this.nutricaoFacade;
	}

	protected MpmItemModeloBasicoDietaDAO getMpmItemModeloBasicoDietaDAO() {
		return mpmItemModeloBasicoDietaDAO;
	}

	protected MpmModeloBasicoModoUsoProcedimentoDAO getMpmModeloBasicoModoUsoProcedimentoDAO() {

		return mpmModeloBasicoModoUsoProcedimentoDAO;
	}	

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
}
