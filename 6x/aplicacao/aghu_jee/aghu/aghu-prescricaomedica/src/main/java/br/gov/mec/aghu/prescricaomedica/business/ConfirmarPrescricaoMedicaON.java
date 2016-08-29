package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.ItemPrescricaoMedica;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnf;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudoConvenio;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.nutricao.business.INutricaoFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualUnfDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmLaudoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoConvenioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConfirmacaoPrescricaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * getParametroFacade Contém as regras de negócio para cofirmação de uma
 * prescrição médica.
 * 
 * @author gmneto
 * 
 */
@Stateless
public class ConfirmarPrescricaoMedicaON extends BaseBusiness {

	@EJB
	private ConfirmarPrescricaoMedicaRN confirmarPrescricaoMedicaRN;
	
	@EJB
	private SolicitacaoHemoterapicaON solicitacaoHemoterapicaON;
	
	@EJB
	private LaudoRN laudoRN;
	
	private static final Log LOG = LogFactory.getLog(ConfirmarPrescricaoMedicaON.class);
	
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
	private MpmCuidadoUsualUnfDAO mpmCuidadoUsualUnfDAO;
	
	@Inject
	private MpmTipoLaudoDAO mpmTipoLaudoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private INutricaoFacade nutricaoFacade;
	
	@Inject
	private MpmLaudoDAO mpmLaudoDAO;
	
	@Inject
	private MpmTipoLaudoConvenioDAO mpmTipoLaudoConvenioDAO;
	
	@Inject
	private MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO;
	
	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;
	
	@Inject
	private MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1386644214246704948L;

	public enum ConfirmarPrescricaoMedicaONExceptionCode implements
			BusinessExceptionCode {
		USUARIO_SEM_REGISTRO, ITENS_DIETA_NAO_PERMITIDOS, VIA_ADMINISTRACAO_NAO_PERMITIDO, CUIDADO_NAO_PERMITIDO, ERRO_JUSTIFICATIVA_SOLICITACAO_HEMOTERAPICA, ERRO_CONFIRMACAO_PRESCRICAO_INVALIDA,
		PRESCRICAO_JA_CANCELADA_POR_OUTRO_USUARIO, MPM_01256
	}

	/**
	 * Método que verifica se o servidor é um médico
	 * e se possui número de CRM. Substitui a verificação antiga
	 * baseada em ações qualificadas e tabelas CSE_* (por exemplo, CSE_ACOES)
	 * usada na função de banco ORACLE: CSEC_VER_ACAO_QUA_MA
	 * 
	 * @param serMatricula
	 * @param serCodigo
	 * @param nomePessoa
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public Boolean verificarServidorMedico(Integer serMatricula, Short serCodigo) throws ApplicationBusinessException {
		getLogger().info("Verificando se é médico.");
		
		BuscaConselhoProfissionalServidorVO vo = getPrescricaoMedicaFacade().buscaConselhoProfissionalServidorVO(serMatricula, serCodigo);

		if (vo.getNumeroRegistroConselho() == null) {
			getLogger().warn("Usuário com matrícula '"+serMatricula+"' e código '"+serCodigo+"' não possui CRM.");
			return false;
		}
		
		getLogger().info("Usuário com matrícula '"+serMatricula+"' e código '"+serCodigo+"' possui CRM.");
		
		return true;
	}
	
	protected void validarSituacaoPrescricao(MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException {
		
		if (!DominioSituacaoPrescricao.U.equals(prescricao.getSituacao())){
			throw new ApplicationBusinessException(
					ConfirmarPrescricaoMedicaONExceptionCode.MPM_01256);
		}
	}


	/**
	 * Método responsável por realizar a lógica relacionada com a ação de
	 * confirmar uma prescrição médica
	 * 
	 * 
	 * @param prescricao
	 * @throws BaseException
	 */
	public ConfirmacaoPrescricaoVO confirmarPrescricaoMedica(MpmPrescricaoMedica prescricao, String nomeMicrocomputador, Date dataFimVinculoServidor)
			throws BaseException {
		ConfirmacaoPrescricaoVO confirmacaoPrescricaoVO = null;
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();

		if (prescricao == null) {
			throw new ApplicationBusinessException(
					ConfirmarPrescricaoMedicaONExceptionCode.ERRO_CONFIRMACAO_PRESCRICAO_INVALIDA);
		}
		
		//prescricao = this.getPrescricaoMedicaDAO().merge(prescricao);
		prescricao = this.getPrescricaoMedicaDAO().obterOriginal(prescricao.getId());
		
		if(prescricao == null) {
			throw new ApplicationBusinessException(
					ConfirmarPrescricaoMedicaONExceptionCode.PRESCRICAO_JA_CANCELADA_POR_OUTRO_USUARIO);
		}
		
		this.validarSituacaoPrescricao(prescricao);
		
		
		prescricao = mpmPrescricaoMedicaDAO.obterPrescricaoComAtendimentoPaciente(prescricao.getId().getAtdSeq(), prescricao.getId().getSeq());
		
		Boolean prescricaoAmbulatorial = getAghuFacade().obterAtendimento(prescricao.getId().getAtdSeq(),null,
				DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial()) != null;

		if (!this.verificarServidorMedico(
					servidorLogado.getId().getMatricula(),
					servidorLogado.getId().getVinCodigo())){
			
			throw new ApplicationBusinessException(
					ConfirmarPrescricaoMedicaONExceptionCode.USUARIO_SEM_REGISTRO,
					servidorLogado.getPessoaFisica().getNome());
		}
		
		getLogger().info(
				"Iniciando a confirmação da prescrição: "
						+ prescricao.getId().getSeq());

		try {

			//this.getPrescricaoMedicaDAO().refresh(prescricao);

			if(!prescricaoAmbulatorial){
				getLogger().info(
						"Chamando validação para item de dieta na unidade da prescrição: "
								+ prescricao.getId().getSeq());
				validarPermissaoItemDietaNaUnidade(prescricao);
				
			}

			getLogger().info(
					"Chamando validação para via de administração na unidade da prescrição: "
							+ prescricao.getId().getSeq());
			validarPermissaoViaAdministracaoNaUnidade(prescricao);

			getLogger().info(
					"Chamando validação para cuidado na unidade da prescrição: "
							+ prescricao.getId().getSeq());
			validarCuidadoNaUnidade(prescricao);

			getLogger().info(
					"Chamando validação para justificativa hemoterapia da prescrição: "
							+ prescricao.getId().getSeq());
			validarJustificativaHemoterapia(prescricao);

			getLogger().info(
					"Chamando geração de laudos da prescrição: "
							+ prescricao.getId().getSeq());
			List<MpmLaudo> laudoList = gerarLaudos(prescricao);
			
			confirmacaoPrescricaoVO = this.getConfirmarPrescricaoMedicaRN().confirmarPrescricaoMedica(prescricao, nomeMicrocomputador, dataFimVinculoServidor);

			LaudoRN laudoRN = this.getLaudoRN();
			laudoRN.inserirListaLaudos(laudoList);
			
			//prescricao = this.getPrescricaoMedicaDAO().merge(prescricao);
			this.getPrescricaoMedicaDAO().atualizar(prescricao);
			
		} catch (BaseRuntimeException e) {
			getLogger().error(e.getMessage(), e);
			throw new ApplicationBusinessException(e.getCode());
		}

		getLogger().info(
				"Finalizada a confirmação da prescrição: "
						+ prescricao.getId().getSeq());
		
		return confirmacaoPrescricaoVO;
	}

	/**
	 * Coordena a geração de laudos.
	 * 
	 * @param prescricao
	 * @throws ApplicationBusinessException
	 */
	private List<MpmLaudo> gerarLaudos(MpmPrescricaoMedica prescricao)
			throws BaseException {

		List<MpmLaudo> laudoList = new ArrayList<MpmLaudo>();

		this.gerarLaudosProcedimentosMateriais(prescricao, laudoList);

		this.gerarLaudosProcedimentosCirurgicos(prescricao, laudoList);

		this.gerarLaudosProcedimentosEspeciaisDiversos(prescricao, laudoList);

		FatConvenioSaudePlano convenioSaudePlano = this
				.getConfirmarPrescricaoMedicaRN().obterConvenioAtendimento(
						prescricao.getAtendimento());

		IParametroFacade parametroFacade = this.getParametroFacade();

		AghParametros seqLaudoUTI = parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_LAUDO_UTI);

		AghParametros seqLaudoMaiorPermanencia = parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_LAUDO_MAIOR_PERM);

		gerarLaudosUTI(prescricao, laudoList, convenioSaudePlano, seqLaudoUTI);

		gerarLaudoPermanenciaMaior(prescricao, laudoList, convenioSaudePlano,
				seqLaudoMaiorPermanencia);

		return laudoList;
	}

	/**
	 * 
	 * Gera laudos do tipo permanencia maior
	 * 
	 * @param prescricao
	 * @param laudoList
	 * @param convenioSaudePlano
	 * @param seqLaudoMaiorPermanencia
	 */
	private void gerarLaudoPermanenciaMaior(MpmPrescricaoMedica prescricao,
			List<MpmLaudo> laudoList, FatConvenioSaudePlano convenioSaudePlano,
			AghParametros seqLaudoMaiorPermanencia) {
		MpmTipoLaudoConvenio tipoLaudoConvenio = this
				.getMpmTipoLaudoConvenioDAO()
				.obterTempoValidadeTipoLaudoPermanenciaMaior(
						seqLaudoMaiorPermanencia.getVlrNumerico().shortValue(),
						convenioSaudePlano);

		if (tipoLaudoConvenio != null) {

			Short quantidadeDiasFaturamento = 0;
			Short diasPermanenciaMaior = 0;

			MpmTipoLaudo tipoLaudoMaiorPermanencia = this.getMpmTipoLaudoDAO()
					.obterPorChavePrimaria(
							seqLaudoMaiorPermanencia.getVlrNumerico()
									.shortValue());

			if (prescricao.getAtendimento().getInternacao() != null) {
				if (prescricao.getAtendimento()
						.getInternacao().getItemProcedimentoHospitalar() != null){
					
					if (prescricao.getAtendimento()
					.getInternacao().getItemProcedimentoHospitalar()
					.getQuantDiasFaturamento() != null){
						quantidadeDiasFaturamento = prescricao.getAtendimento()
						.getInternacao().getItemProcedimentoHospitalar()
						.getQuantDiasFaturamento();						
					}

					diasPermanenciaMaior = prescricao.getAtendimento()
					.getInternacao().getItemProcedimentoHospitalar()
					.getDiasPermanenciaMaior();					
				}
			}
			
			if (diasPermanenciaMaior == null || diasPermanenciaMaior > 0){
				
				Integer adicionalDias = 0;
				if (quantidadeDiasFaturamento != null) {
					adicionalDias = (quantidadeDiasFaturamento * 2) + 1;
				}
				
				if (DateUtil.validaDataTruncadaMaiorIgual(new Date(), DateUtil
						.adicionaDias(prescricao.getAtendimento().getInternacao()
								.getDthrInternacao(), adicionalDias))) {
					
					if (this.getMpmLaudoDAO()
							.obterCountLaudosPorTipoEAtendimento(
									prescricao.getAtendimento(),
									tipoLaudoMaiorPermanencia) <= 0) {
						
						MpmLaudo laudo = new MpmLaudo();
						laudo.setDthrInicioValidade(prescricao.getAtendimento()
								.getInternacao().getDthrInternacao());
						laudo.setContaDesdobrada(false);
						laudo.setImpresso(false);
						laudo.setLaudoManual(false);
						laudo.setAtendimento(prescricao.getAtendimento());
						laudo.setTipoLaudo(tipoLaudoMaiorPermanencia);
						
						laudoList.add(laudo);
						
					}
				}
			}
		}
	}

	/**
	 * Gera laudos do tipo UTI
	 * 
	 * @param prescricao
	 * @param laudoList
	 * @param convenioSaudePlano
	 * @param seqLaudoUTI
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void gerarLaudosUTI(MpmPrescricaoMedica prescricao,
			List<MpmLaudo> laudoList, FatConvenioSaudePlano convenioSaudePlano,
			AghParametros seqLaudoUTI) {

		IAghuFacade aghuFacade = getAghuFacade();
		if (prescricao.getAtendimento().getUnidadeFuncional() != null) {
			// TODO: RETIRAR ESTE MERGE QUANDO AS EXCEPTIONS COM ROLLBACK DOS
			// CRUDS DA PRESCRIÇÃO JÁ TIVEREM
			// SIDO REMOVIDAS.
			aghuFacade.atualizarAghUnidadesFuncionaisSemException(prescricao.getAtendimento().getUnidadeFuncional());
		}

		boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(prescricao.getAtendimento().getUnidadeFuncional().getSeq(),
																							ConstanteAghCaractUnidFuncionais.LAUDO_CTI);
		if (possuiCaracteristica) {

			Short tempoValidade = this.getMpmTipoLaudoConvenioDAO()
					.obterTempoValidadeTipoLaudo(
							seqLaudoUTI.getVlrNumerico().shortValue(),
							convenioSaudePlano);

			if (tempoValidade != null) {

				boolean acheiLaudoValido = false;

				MpmTipoLaudo tipoLaudoUTI = this.getMpmTipoLaudoDAO()
						.obterPorChavePrimaria(
								seqLaudoUTI.getVlrNumerico().shortValue());

				List<MpmLaudo> laudos = this.getMpmLaudoDAO()
						.listarLaudosPorAtendimentoETipo(
								prescricao.getAtendimento(), tipoLaudoUTI,
								prescricao.getDthrInicio());

				String justificativa = null;
				Short duracaoTratamentoSolicitado = null;

				for (MpmLaudo laudo : laudos) {
					Date dataTeste = DateUtil.adicionaDias(laudo
							.getDthrInicioValidade(), tempoValidade - 1);

					if (DateUtil.validaDataTruncadaMaiorIgual(dataTeste,
							prescricao.getDthrInicio())
							&& laudo.getDthrFimValidade() == null) {

						acheiLaudoValido = true;

					} else if (laudo.getDthrFimValidade() != null
							&& DateUtil.entreTruncado(prescricao
									.getDthrInicio(), laudo
									.getDthrInicioValidade(), laudo
									.getDthrFimValidade())) {

						acheiLaudoValido = true;

					} else {
						if (laudo.getDthrFimValidade() == null) {
							laudo.setDthrFimValidade(DateUtil.adicionaDias(
									laudo.getDthrInicioValidade(),
									tempoValidade - 1));
						}
					}

					justificativa = laudo.getJustificativa();
					duracaoTratamentoSolicitado = laudo
							.getDuracaoTratSolicitado();

				}

				if (!acheiLaudoValido) {

					Short maximaDiariaUTI = null;

					if (prescricao.getAtendimento().getInternacao() != null) {
						maximaDiariaUTI = prescricao.getAtendimento()
								.getInternacao()
								.getItemProcedimentoHospitalar()
								.getMaxDiariaUti();
					}

					if (maximaDiariaUTI == null || maximaDiariaUTI > 0) {
						MpmLaudo laudo = new MpmLaudo();
						laudo.setDthrInicioValidade(prescricao.getDthrInicio());
						laudo.setDthrFimValidade(DateUtil.adicionaDias(laudo
								.getDthrInicioValidade(), tempoValidade - 1));
						laudo.setDthrFimPrevisao(DateUtil.adicionaDias(laudo
								.getDthrInicioValidade(), tempoValidade - 1));
						laudo.setJustificativa(justificativa);
						laudo.setContaDesdobrada(false);
						laudo.setImpresso(false);
						laudo
								.setDuracaoTratSolicitado(duracaoTratamentoSolicitado);
						laudo.setLaudoManual(false);
						laudo.setAtendimento(prescricao.getAtendimento());
						laudo.setTipoLaudo(tipoLaudoUTI);
						laudoList.add(laudo);
					}

				}
			}
		}
	}

	/**
	 * Gera laudos para proceidmentos cirurgicos.
	 * 
	 * @param prescricao
	 * @param laudoList
	 * @throws ApplicationBusinessException 
	 */
	private void gerarLaudosProcedimentosCirurgicos(
			MpmPrescricaoMedica prescricao, List<MpmLaudo> laudoList) throws ApplicationBusinessException {

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> procedimentosMap = this
				.getConfirmarPrescricaoMedicaRN()
				.listarProcedimentosCirurgicosGeracaoLaudos(prescricao);

		MpmLaudo laudo = null;

		for (MpmPrescricaoProcedimento procedimento : procedimentosMap.keySet()) {

			laudo = new MpmLaudo();
			laudo.setDthrInicioValidade(prescricao.getDthrInicio());

			Date dataFimValidade = prescricao.getDthrInicio();
			if (procedimento.getDuracaoTratamentoSolicitado() != null) {
				dataFimValidade = DateUtil.adicionaDias(dataFimValidade,
						procedimento.getDuracaoTratamentoSolicitado()
								.intValue() - 1);
			}
			laudo.setDthrFimValidade(dataFimValidade);
			laudo.setDthrFimPrevisao(dataFimValidade);

			laudo.setJustificativa(procedimento.getJustificativa());
			laudo.setContaDesdobrada(false);
			laudo.setImpresso(false);
			laudo.setDuracaoTratSolicitado(procedimento
					.getDuracaoTratamentoSolicitado());
			laudo.setLaudoManual(false);
			laudo.setAtendimento(prescricao.getAtendimento());
			laudo.setPrescricaoProcedimento(procedimento);
			laudo.setProcedimentoHospitalarInterno(procedimentosMap
					.get(procedimento));

			this.adicionarLaudoLista(laudoList, laudo);

		}

	}

	/**
	 * Gera laudo para procedimentos epeciais diversos.
	 * 
	 * @param prescricao
	 * @param laudoList
	 * @throws ApplicationBusinessException
	 */
	private void gerarLaudosProcedimentosEspeciaisDiversos(
			MpmPrescricaoMedica prescricao, List<MpmLaudo> laudoList)
			throws BaseException {

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> procedimentosMap = this
				.getConfirmarPrescricaoMedicaRN()
				.listarProcedimentosDiversosGeracaoLaudos(prescricao);

		MpmLaudo laudo = null;

		IParametroFacade parametroFacade = this.getParametroFacade();

		int codigoHemodialise = parametroFacade
				.buscarAghParametro(
						AghuParametrosEnum.P_AGHU_PROCEDIMENTO_HOSPITALAR_INTERNO_HEMODIALISE)
				.getVlrNumerico().intValue();
		int codigoDialise = parametroFacade
				.buscarAghParametro(
						AghuParametrosEnum.P_AGHU_PROCEDIMENTO_HOSPITALAR_INTERNO_DIALISE)
				.getVlrNumerico().intValue();

		for (MpmPrescricaoProcedimento procedimento : procedimentosMap.keySet()) {

			Integer codigoProcedimentoInterno = procedimentosMap.get(
					procedimento).getSeq();
			boolean gerarLaudo = true;

			if (codigoProcedimentoInterno == codigoHemodialise
					|| codigoProcedimentoInterno == codigoDialise) {
				gerarLaudo = !this.getConfirmarPrescricaoMedicaRN()
						.verificarPacienteContaApac();

			}

			if (gerarLaudo) {

				laudo = new MpmLaudo();
				laudo.setDthrInicioValidade(prescricao.getDthrInicio());

				Date dataFimValidade = prescricao.getDthrInicio();
				if (procedimento.getDuracaoTratamentoSolicitado() != null) {
					dataFimValidade = DateUtil.adicionaDias(dataFimValidade,
							procedimento.getDuracaoTratamentoSolicitado()
									.intValue() - 1);
				}
				laudo.setDthrFimValidade(dataFimValidade);
				laudo.setDthrFimPrevisao(dataFimValidade);

				laudo.setJustificativa(procedimento.getJustificativa());
				laudo.setContaDesdobrada(false);
				laudo.setImpresso(false);
				// #51803
				if (procedimento.getDuracaoTratamentoSolicitado() == null
						|| procedimento.getDuracaoTratamentoSolicitado() == 0) {
					laudo.setDuracaoTratSolicitado((short) 1);
				}
				else {
					laudo.setDuracaoTratSolicitado(procedimento
							.getDuracaoTratamentoSolicitado());
				}
				laudo.setLaudoManual(false);
				laudo.setAtendimento(prescricao.getAtendimento());
				laudo.setPrescricaoProcedimento(procedimento);
				laudo.setProcedimentoHospitalarInterno(procedimentosMap
						.get(procedimento));

				this.adicionarLaudoLista(laudoList, laudo);

			}

		}

	}

	/**
	 * Gera laudos para procedimentos de acordo com o material
	 * 
	 * @param prescricao
	 * @param laudoList
	 * @throws ApplicationBusinessException 
	 */
	private void gerarLaudosProcedimentosMateriais(
			MpmPrescricaoMedica prescricao, List<MpmLaudo> laudoList) throws ApplicationBusinessException {

		Map<MpmPrescricaoProcedimento, FatProcedHospInternos> procedimentosMap = this
				.getConfirmarPrescricaoMedicaRN()
				.listarProcedimentosGeracaoLaudos(prescricao);

		MpmLaudo laudo = null;

		for (MpmPrescricaoProcedimento procedimento : procedimentosMap.keySet()) {
			laudo = new MpmLaudo();
			laudo.setDthrInicioValidade(prescricao.getDthrInicio());

			Date dataFimValidade = prescricao.getDthrInicio();
			if (procedimento.getDuracaoTratamentoSolicitado() != null) {
				dataFimValidade = DateUtil.adicionaDias(dataFimValidade,
						procedimento.getDuracaoTratamentoSolicitado()
								.intValue() - 1);
			}
			laudo.setDthrFimValidade(dataFimValidade);
			laudo.setDthrFimPrevisao(dataFimValidade);

			laudo.setJustificativa(procedimento.getJustificativa());
			laudo.setContaDesdobrada(false);
			laudo.setImpresso(false);
			laudo.setDuracaoTratSolicitado(procedimento
					.getDuracaoTratamentoSolicitado());
			laudo.setLaudoManual(false);
			laudo.setAtendimento(prescricao.getAtendimento());
			laudo.setPrescricaoProcedimento(procedimento);
			laudo.setProcedimentoHospitalarInterno(procedimentosMap
					.get(procedimento));

			this.adicionarLaudoLista(laudoList, laudo);

		}

	}

	private void adicionarLaudoLista(List<MpmLaudo> laudoList, MpmLaudo laudo) {
		boolean achouProcedimento = false;
		for (MpmLaudo laudos : laudoList) {
			if (laudos.getPrescricaoProcedimento().equals(
					laudo.getPrescricaoProcedimento())) {
				achouProcedimento = true;
			}
		}

		if (!achouProcedimento) {
			laudoList.add(laudo);
		}

	}

	/**
	 * @param prescricao
	 * @throws ApplicationBusinessException
	 */
	private void validarCuidadoNaUnidade(MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException {
		// Permissão de cuidados na unidade.
		List<MpmPrescricaoCuidado> listaCuidadosMedicos = null;
		listaCuidadosMedicos = getPrescricaoCuidadoDAO()
				.pesquisarCuidadosMedicos(prescricao.getId(),
						prescricao.getDthrFim(), false);

		boolean possuiCuidadoNaoPermitido = false;
		StringBuilder cuidadosNaoPermitidos = new StringBuilder();

		MpmCuidadoUsualUnfDAO mpmCuidadoUsualUnfDAO = getMpmCuidadoUsualUnfDAO();

		for (Iterator<MpmPrescricaoCuidado> iteradorPrescricaoCuidado = listaCuidadosMedicos
				.iterator(); iteradorPrescricaoCuidado.hasNext();) {
			MpmPrescricaoCuidado cuidados = iteradorPrescricaoCuidado.next();

			List<MpmCuidadoUsualUnf> listaMpmCuidadoUsualUnf = mpmCuidadoUsualUnfDAO
					.listarMpmCuidadoUsualUnfPorUnidadeFuncionalCuidado(
							cuidados.getMpmCuidadoUsuais(), prescricao
									.getAtendimento().getUnidadeFuncional());

			if (listaMpmCuidadoUsualUnf.isEmpty()) {
				possuiCuidadoNaoPermitido = true;

				if (cuidados.getDescricaoFormatada() != null
						&& !cuidados.getDescricaoFormatada().isEmpty()) {
					cuidadosNaoPermitidos.append("["
							+ cuidados.getDescricaoFormatada() + "]");
				} else {
					cuidadosNaoPermitidos
							.append("[" + cuidados.getDescricao() != null ? cuidados
									.getDescricao()
									: cuidados.getId() + "]");
				}

				if (iteradorPrescricaoCuidado.hasNext()) {
					cuidadosNaoPermitidos.append(" ,");
				}
			}
		}

		if (possuiCuidadoNaoPermitido) {
			throw new ApplicationBusinessException(
					ConfirmarPrescricaoMedicaONExceptionCode.CUIDADO_NAO_PERMITIDO,
					cuidadosNaoPermitidos);
		}
	}

	/**
	 * @param prescricao
	 * @throws ApplicationBusinessException
	 */
	private void validarPermissaoViaAdministracaoNaUnidade(
			MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException {
		// permissão de via de administração paramedicamentos na unidade.
		IFarmaciaFacade farmaciaFacade = getFarmaciaFacade();

		List<AfaViaAdmUnf> listaAfaViaAdmUnfUnidade = farmaciaFacade
				.listarAfaViaAdmUnfAtivasPorUnidadeFuncional(prescricao
						.getAtendimento().getUnidadeFuncional());

		if (!listaAfaViaAdmUnfUnidade.isEmpty()) {
			boolean possuiViaAdministracaoNaoPermitida = false;
			StringBuilder viaAdministracao = new StringBuilder();

			List<MpmPrescricaoMdto> listaMedicamentos = this
					.getPrescricaoMdtoDAO()
					.obterListaMedicamentosPrescritosPelaChavePrescricao(
							prescricao.getId(), prescricao.getDthrFim());

			for (MpmPrescricaoMdto prescricaoMedicamento : listaMedicamentos) {
				List<AfaViaAdmUnf> listaAfaViaAdmUnfUnidadeViaAdministracao = farmaciaFacade
						.listarAfaViaAdmUnfAtivasPorUnidadeFuncionalEViaAdministracao(
								prescricao.getAtendimento()
										.getUnidadeFuncional(),
								prescricaoMedicamento.getViaAdministracao());

				if (listaAfaViaAdmUnfUnidadeViaAdministracao.isEmpty()) {
					possuiViaAdministracaoNaoPermitida = true;
					viaAdministracao.append(prescricaoMedicamento
							.getViaAdministracao().getDescricao());
					viaAdministracao.append(" ,");
				}
			}

			if (possuiViaAdministracaoNaoPermitida) {
				throw new ApplicationBusinessException(
						ConfirmarPrescricaoMedicaONExceptionCode.VIA_ADMINISTRACAO_NAO_PERMITIDO,
						viaAdministracao);
			}

		}
	}

	/**
	 * @param prescricao
	 * @throws ApplicationBusinessException
	 */
	private void validarPermissaoItemDietaNaUnidade(
			MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException {
		// permissão da unidade para itens da dieta.
		List<MpmPrescricaoDieta> dietas = this.getPrescricaoDietaDAO()
				.buscaDietaPorPrescricaoMedica(prescricao.getId(),
						prescricao.getDthrFim(), false);

		INutricaoFacade nutricaoFacade = this.getNutricaoFacade();
		boolean possuiItensDietaNaoPermitidos = false;
		StringBuilder itensDietaNaoPermitidos = new StringBuilder();

		for (MpmPrescricaoDieta dieta : dietas) {
			for (MpmItemPrescricaoDieta itemDieta : dieta
					.getItemPrescricaoDieta()) {

				List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs = nutricaoFacade
						.listarAnuTipoItemDietaUnfsPorTipoItemEUnidadeFuncional(
								itemDieta.getTipoItemDieta(), prescricao
										.getAtendimento().getUnidadeFuncional());

				if (listaAnuTipoItemDietaUnfs.isEmpty()) {
					possuiItensDietaNaoPermitidos = true;
					itensDietaNaoPermitidos.append(itemDieta.getTipoItemDieta()
							.getDescricao());
					itensDietaNaoPermitidos.append(", ");

				}

			}
		}

		if (possuiItensDietaNaoPermitidos) {
			throw new ApplicationBusinessException(
					ConfirmarPrescricaoMedicaONExceptionCode.ITENS_DIETA_NAO_PERMITIDOS,
					itensDietaNaoPermitidos);
		}
	}

	private void validarJustificativaHemoterapia(MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException {

		SolicitacaoHemoterapicaON solicitacaoHemoterapicaON = getSolicitacaoHemoterapicaON();
		List<AbsSolicitacoesHemoterapicas> listaSolicitacoesHemoterapicas = solicitacaoHemoterapicaON
				.obterListaSolicitacoesHemoterapicasPelaChavePrescricao(prescricao
						.getId());
		if (listaSolicitacoesHemoterapicas != null
				&& !listaSolicitacoesHemoterapicas.isEmpty()) {
			for (AbsSolicitacoesHemoterapicas solicitacaoHemoterapica : listaSolicitacoesHemoterapicas) {
				List<AbsItensSolHemoterapicas> listaItensSolicitacoesHemoterapicas = solicitacaoHemoterapica
						.getItensSolHemoterapicas();
				if (listaItensSolicitacoesHemoterapicas != null
						&& !listaItensSolicitacoesHemoterapicas.isEmpty()) {
					for (AbsItensSolHemoterapicas item : listaItensSolicitacoesHemoterapicas) {
						if (((item.getComponenteSanguineo() != null && item
								.getComponenteSanguineo().getIndJustificativa()) || (item
								.getProcedHemoterapico() != null && item
								.getProcedHemoterapico().getIndJustificativa()))
								&& !verificaJustificativas(item)) {
							throw new ApplicationBusinessException(
									ConfirmarPrescricaoMedicaONExceptionCode.ERRO_JUSTIFICATIVA_SOLICITACAO_HEMOTERAPICA);
						}
					}
				}
			}
		}
	}

	private Boolean verificaJustificativas(AbsItensSolHemoterapicas itemSolHemo) {
		boolean encontrou = false;

		if (itemSolHemo.getComponenteSanguineo() != null) {
			for (AbsItemSolicitacaoHemoterapicaJustificativa itemJustificativa : itemSolHemo
					.getItemSolicitacaoHemoterapicaJustificativas()) {
				if (itemJustificativa.getMarcado()) {
					encontrou = true;
					break;
				}
			}
		}
		if (itemSolHemo.getProcedHemoterapico() != null) {
			for (AbsItemSolicitacaoHemoterapicaJustificativa itemJustificativa : itemSolHemo
					.getItemSolicitacaoHemoterapicaJustificativas()) {
				if (itemJustificativa.getMarcado()) {
					encontrou = true;
					break;
				}
			}
		}

		return encontrou;
	}
	
	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritosConfirmadosPelaChavePrescricao(
			MpmPrescricaoMedicaId prescricao, Date dtHrFimPrescricaoMedica,
			Boolean isSolucao) {
		List<MpmPrescricaoMdto> lista =  getPrescricaoMdtoDAO().obterListaMedicamentosPrescritosConfirmadosPelaChavePrescricao(prescricao, dtHrFimPrescricaoMedica, isSolucao);
		
		for (MpmPrescricaoMdto mpmPrescricaoMdto : lista) {
			mpmPrescricaoMdto.getDescricaoFormatada(); 
			if(mpmPrescricaoMdto.getItensPrescricaoMdtos() != null){
				mpmPrescricaoMdto.getItensPrescricaoMdtos().size();
				for (MpmItemPrescricaoMdto mpmItemPrescricaoMdto : mpmPrescricaoMdto.getItensPrescricaoMdtos()) {
					mpmItemPrescricaoMdto.getDescricaoFormatada();
				}
			}
		}
		return lista;
	}
	
	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritos(
			MpmPrescricaoMedicaId prescricao, Date dtHrFimPrescricaoMedica,
			Boolean isSolucao) {
		List<MpmPrescricaoMdto> lista =  this.getPrescricaoMdtoDAO().obterListaMedicamentosPrescritos(prescricao, dtHrFimPrescricaoMedica, isSolucao);
		for (MpmPrescricaoMdto mpmPrescricaoMdto : lista) {
			mpmPrescricaoMdto.possuiFilhos();
			mpmPrescricaoMdto.getDescricaoFormatada();
			mpmPrescricaoMdto.getIndAntiMicrobiano();
			mpmPrescricaoMdto.getPrescricaoMdtoOrigem();
			mpmPrescricaoMdto.getItensPrescricaoMdtos();
			mpmPrescricaoMdto.getViaAdministracao();
		}
		return lista;
	}
	
	public MpmPrescricaoMedica obterPrescricaoPorChavePrimaria(MpmPrescricaoMedicaId mpmId) {
		MpmPrescricaoMedica mpmPrescricaoMedica = getPrescricaoMedicaDAO().obterPorChavePrimaria(mpmId);
		
		if(mpmPrescricaoMedica != null && mpmPrescricaoMedica.getAtendimento()!= null){
			if(mpmPrescricaoMedica.getAtendimento().getUnidadeFuncional()!=null){
				mpmPrescricaoMedica.getAtendimento().getUnidadeFuncional().getDescricao();
			}
		}
		return mpmPrescricaoMedica;
	}	
	
	public List<ItemPrescricaoMedica> listarItensPrescricaoMedicaConfirmados(
			MpmPrescricaoMedica prescricao) {
		
		List<ItemPrescricaoMedica> lista = this.getPrescricaoMedicaDAO().listarItensPrescricaoMedicaConfirmados(prescricao);
		for (ItemPrescricaoMedica itemPrescricaoMedica : lista) {
			itemPrescricaoMedica.possuiFilhos();
		}
		
		return lista;
	}

	protected INutricaoFacade getNutricaoFacade() {
		return nutricaoFacade;
	}

	protected MpmPrescricaoDietaDAO getPrescricaoDietaDAO() {
		return mpmPrescricaoDietaDAO;
	}	
	
	protected IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	/**
	 * Retorna o DAO de prescrição médica.
	 * 
	 * @return
	 */
	protected MpmPrescricaoMedicaDAO getPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}

	protected MpmPrescricaoMdtoDAO getPrescricaoMdtoDAO() {
		return mpmPrescricaoMdtoDAO;

	}

	protected MpmPrescricaoCuidadoDAO getPrescricaoCuidadoDAO() {
		return mpmPrescricaoCuidadoDAO;
	}

	protected MpmCuidadoUsualUnfDAO getMpmCuidadoUsualUnfDAO() {
		return mpmCuidadoUsualUnfDAO;
	}

	protected ConfirmarPrescricaoMedicaRN getConfirmarPrescricaoMedicaRN() {
		return confirmarPrescricaoMedicaRN;
	}

	// LaudoRN já existia como um componente seam. verificar necessidade de
	// migração p/ nova arquitetura.
	protected LaudoRN getLaudoRN() {
		return laudoRN;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected SolicitacaoHemoterapicaON getSolicitacaoHemoterapicaON() {
		return solicitacaoHemoterapicaON;
	}

	protected MpmTipoLaudoConvenioDAO getMpmTipoLaudoConvenioDAO() {
		return mpmTipoLaudoConvenioDAO;
	}

	protected MpmLaudoDAO getMpmLaudoDAO() {
		return mpmLaudoDAO;
	}

	protected MpmTipoLaudoDAO getMpmTipoLaudoDAO() {
		return mpmTipoLaudoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	

	
}
