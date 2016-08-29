package br.gov.mec.aghu.paciente.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.checagemeletronica.business.IChecagemEletronicaFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioControleSumarioAltaPendente;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioPermiteManuscrito;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoPim2;
import br.gov.mec.aghu.dominio.DominioSituacaoSaps3;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentoPacientesId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmEscoreSaps3;
import br.gov.mec.aghu.model.MpmPim2;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe que implementa toda a lógica encontrada na enforce das triggers da
 * tabela AGH_ATENDIMENTOS.
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class AtendimentoEnforceRN extends BaseBusiness {

	@EJB
	private AtendimentosRN atendimentosRN;
	
	@EJB
	private AtendimentoPacienteRN atendimentoPacienteRN;
	
	private static final Log LOG = LogFactory.getLog(AtendimentoEnforceRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IChecagemEletronicaFacade checagemEletronicaFacade;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4311036934634986941L;
	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	

	/**
	 * Enumeracao com os codigos de mensagens de exceções negociais.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum AtendimentoEnforceRNExceptionCode implements BusinessExceptionCode {
		AGH_00709, AGH_00710, PRONT_NAO_PERTENCE, ERRO_INSERIR_ALTA_SUMARIO, ERRO_ATUALIZAR_ALTA_SUMARIO
	}

	/**
	 * Método para gerar o PIM2
	 * 
	 * @param atdSeq
	 * @param dthrIngressoUnidade
	 * @throws ApplicationBusinessException
	 */
	private void gerarPim2(Integer atdSeq, Date dthrIngressoUnidade) throws ApplicationBusinessException {
		IPrescricaoMedicaFacade prescricaoMedicaFacade = getPrescricaoMedicaFacade();
		
		MpmPim2 pim2 = new MpmPim2();
		pim2.setAtendimento(this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq));
		pim2.setDthrIngressoUnidade(dthrIngressoUnidade);
		pim2.setSituacao(DominioSituacaoPim2.P);
		
		//this.getPim2RN().inserirPim2(pim2);
		//chamada deve ser via fachada, pois é outro módulo
		prescricaoMedicaFacade.inserirPim2(pim2);
	}

	/**
	 * Método para finaliza o PIM2. Para finalizar todos os PIM2 pendentes deve
	 * haver ao menos um registro aonde a informação de ingresso na unidade seja
	 * inferior a 24 horas (parâmetro de sistema) em relação a data e hora de
	 * ingresso na unidade
	 * 
	 * p_dthr_evento - dthr_ingresso_unidade < 1 ( o mesmo que < 24 horas)
	 * 
	 * @param atdSeq
	 * @param dthrEvento
	 * @param limiteHrsPim2
	 * @param situacao
	 * @throws ApplicationBusinessException
	 */
	private void finalizarPim2(Integer atdSeq, Date dthrEvento, Integer limiteHrsPim2, DominioSituacaoPim2 situacao)
			throws ApplicationBusinessException {

		List<MpmPim2> listaPim2 = this.getPrescricaoMedicaFacade().pesquisarPim2PorAtendimentoSituacao(atdSeq, situacao);

		boolean existe = false;
		for (MpmPim2 pim2 : listaPim2) {
			double limiteHoras  = (double)limiteHrsPim2 / (double)24;
			BigDecimal diff = DateUtil.calcularDiasEntreDatasComPrecisao(pim2.getDthrIngressoUnidade(), dthrEvento);
			
			if (diff.doubleValue() < limiteHoras) {
				existe = true;
				break;
			}
		}

		if (existe) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			// Finalizar os PIM2 pendentes
			IPrescricaoMedicaFacade prescricaoMedicaFacade = getPrescricaoMedicaFacade();
			MpmPim2 pim2Old = null;
			for (MpmPim2 pim2 : listaPim2) {
				pim2Old = prescricaoMedicaFacade.clonarPim2(pim2);
				pim2.setSituacao(DominioSituacaoPim2.A);
				pim2.setServidor(servidorLogado);
				
				//this.getPim2RN().atualizarPim2(pim2, pim2Old);
				//chamada deve ser via fachada, pois é outro módulo
				prescricaoMedicaFacade.atualizarPim2(pim2, pim2Old);
				
			}
		}
	}

	/**
	 * OPRADB Procedure AGHK_ATD_RN_2.RN_ATDP_ATU_PIM2
	 * 
	 * @param oper
	 * @param origem
	 * @param atdSeq
	 * @param oldUnfSeq
	 * @param newUnfSeq
	 * @param oldDthrFim
	 * @param newDthrFim
	 * @param dthrIngressoUnidade
	 * @throws ApplicationBusinessException
	 */
	public void atualizarInformacoesFormularioPim2(String oper, DominioOrigemAtendimento origem, Integer atdSeq,
			Short oldUnfSeq, Short newUnfSeq, Date oldDthrFim, Date newDthrFim, Date dthrIngressoUnidade)
			throws ApplicationBusinessException {
		
		try {
			
		
			final String valorPadrao = "X";
			Integer limiteHorasPim2 = 0;
			AghParametros parametroLimiteHoras = null;
	
			// Busca o limite de horas para o PIM2
			try {
				parametroLimiteHoras = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_LIMITE_PIM2_ISENTO);
				limiteHorasPim2 = parametroLimiteHoras.getVlrNumerico().intValue();
			} catch (Exception e) {
				LOG.error("Exceção capturada: ", e);
				// Caso ocorra exceção, é considerado o valor 24
				limiteHorasPim2 = 24;
			}
	
			String oldUnfSeqUtiP;
			String newUnfSeqUtiP;
	
			if (oldUnfSeq == null) {
				oldUnfSeqUtiP = valorPadrao;
			} else {
				oldUnfSeqUtiP = this.getPesquisaInternacaoFacade().verificarCaracteristicaDaUnidadeFuncional(oldUnfSeq,
						ConstanteAghCaractUnidFuncionais.UNID_UTIP).toString();
			}
	
			if (newUnfSeq == null) {
				newUnfSeqUtiP = valorPadrao;
			} else {
				newUnfSeqUtiP = this.getPesquisaInternacaoFacade().verificarCaracteristicaDaUnidadeFuncional(newUnfSeq,
						ConstanteAghCaractUnidFuncionais.UNID_UTIP).toString();
			}
	
			if ("I".equals(oper)) {
				// I --> inserção do atendimento do paciente
	
				// Se origem do atendimento for internação e for em uma undidade que
				// tenha a característica de Unid Utip gerar PIM2
				if (origem.equals(DominioOrigemAtendimento.I) && "S".equals(newUnfSeqUtiP)) {
					this.gerarPim2(atdSeq, dthrIngressoUnidade);
				}
	
			} else if ("A".equals(oper)) {
				// A --> alteração do atendimento do paciente
	
				// Entrada na UTIP. Se a origem do atendimento for uma internação e
				// está entrando em uma unidade que tenha a característica de Unid
				// Utip gerar PIM2
				if (origem.equals(DominioOrigemAtendimento.I) && "N".equals(oldUnfSeqUtiP) && "S".equals(newUnfSeqUtiP)) {
					this.gerarPim2(atdSeq, dthrIngressoUnidade);
				}
	
				// Saída da UTIP. Se a origem do atendimento for uma internação e
				// está saindo de uma unidade que tenha a característica de Unid
				// Utip finalizar PIM2
				if (origem.equals(DominioOrigemAtendimento.I) && "S".equals(oldUnfSeqUtiP) && "N".equals(newUnfSeqUtiP)) {
					this.finalizarPim2(atdSeq, new Date(), limiteHorasPim2, DominioSituacaoPim2.valueOf("P"));
				}
	
				// Alta da UTIP. Se a origem do atendimento for uma internação, a
				// unidade atual de
				// atendimento tem a característica de Unid Utip e está sendo
				// informada a data de fim deste atendimento então deve-se finalizar
				// o PIM2
				if (origem.equals(DominioOrigemAtendimento.I) && "S".equals(newUnfSeqUtiP) && newDthrFim != null) {
					this.finalizarPim2(atdSeq, new Date(), limiteHorasPim2, DominioSituacaoPim2.valueOf("P"));
				}
			}

		} catch (InactiveModuleException e) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			getObjetosOracleDAO().atualizarInformacoesFormularioPim2(oper, origem, atdSeq, oldUnfSeq, newUnfSeq, 
																	  oldDthrFim, newDthrFim, dthrIngressoUnidade, 
																	  servidorLogado);
		}
	}

	/**
	 * Retorna o número de horas que o paciente está internado em uma unidade de
	 * CTI. Calcula a partir da data/hora de ingresso da ficha APACHE, esta
	 * data/hora é a dthr da primeira vez que o paciente ingressou em uma
	 * unidade CTI, não leva em conta transferencias entre ctis/leitos de ctis
	 * 
	 * ORADB FUNCTION MPMC_APACHE_CTIHR
	 * 
	 * @param atdSeq
	 */
	public Float obterNumeroHorasInternado(Integer atdSeq) {
		List<Date> l = this.getPrescricaoMedicaFacade().listarDataIngressoUnidadeOrdenadoPorSeqpDesc(atdSeq);

		if (l != null && !l.isEmpty()) {
			Date dtHrIngressoUnidade = l.get(0);

			dtHrIngressoUnidade = DateUtils.truncate(dtHrIngressoUnidade, Calendar.MINUTE);

			Date dtHrAtual = DateUtils.truncate(new Date(), Calendar.MINUTE);

			Float diff = DateUtil.diffInDays(dtHrAtual, dtHrIngressoUnidade);

			diff = diff * 24;

			BigDecimal res = new BigDecimal(diff).setScale(2, RoundingMode.HALF_DOWN);

			return res.floatValue();
		}

		return 0f;
	}

	/**
	 * ORADB PROCEDURE insere_alta_sumario
	 * 
	 * Método que insere sumario com alta manual
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void inserirAltaSumario(DominioOrigemAtendimento newOrigem, Integer newIntSeq, Integer newAtuSeq,
			Integer newHodSeq, Integer newAtdSeqMae, Integer newPacCodigo, Short newUnfSeq, Integer newAtdSeq,
			Short newEspSeq, Integer vSerMatricula, Short vSerVinCodigo, DominioPermiteManuscrito vIndPermiteManuscrito)
			throws ApplicationBusinessException {

		Short vCspCnvCodigo = null;
		Byte vCspSeq = null;
		String vTamCodigo = null;
		Date vDtIniAtd = null;
		DominioOrigemAtendimento vOrigem;
		Integer vIntSeq;
		Integer vAtuSeq;
		String vAltaObito = null;
		String vAltaObito48 = null;
		DominioIndTipoAltaSumarios vIndtipo;
		Integer vIdadeDias;

		if (newOrigem != null) {
			switch (newOrigem) {
			case I:
				// CURSOR cur_int
				List l = getInternacaoFacade().executarCursorInt(newIntSeq);

				if (l != null && !l.isEmpty()) {
					Object[] obj = (Object[]) l.get(0);
					vCspSeq = (Byte) obj[0];
					vCspCnvCodigo = (Short) obj[1];
					vDtIniAtd = (Date) obj[2];
					vTamCodigo = (String) obj[3];
				}
				break;
			case U:
				// CURSOR cur_atu
				l = getInternacaoFacade().executarCursorAtu(newAtuSeq);

				if (l != null && !l.isEmpty()) {
					Object[] obj = (Object[]) l.get(0);
					vCspSeq = (Byte) obj[0];
					vCspCnvCodigo = (Short) obj[1];
					vDtIniAtd = (Date) obj[2];
					vTamCodigo = (String) obj[3];
				}

				break;
			case H:
				// CURSOR cur_hod
				l = getInternacaoFacade().executarCursorHod(newHodSeq);

				if (l != null && !l.isEmpty()) {
					Object[] obj = (Object[]) l.get(0);
					vCspSeq = (Byte) obj[0];
					vCspCnvCodigo = (Short) obj[1];
					vDtIniAtd = (Date) obj[2];
					vTamCodigo = (String) obj[3];
				}
				break;
			case N:
				// CURSOR c_atd_mae
				l = getAghuFacade().executarCursorAtdMae(newAtdSeqMae);

				if (l != null && !l.isEmpty()) {
					Object[] obj = (Object[]) l.get(0);
					vOrigem = (DominioOrigemAtendimento) obj[0];
					vIntSeq = (Integer) obj[1];
					vAtuSeq = (Integer) obj[2];

					switch (vOrigem) {
					case I:
						// CURSOR cur_int
						l = getInternacaoFacade().executarCursorInt(vIntSeq);

						if (l != null && !l.isEmpty()) {
							obj = (Object[]) l.get(0);
							vCspSeq = (Byte) obj[0];
							vCspCnvCodigo = (Short) obj[1];
							vDtIniAtd = (Date) obj[2];
							vTamCodigo = (String) obj[3];
						}
						break;
					case U:
						// CURSOR cur_atu
						l = getInternacaoFacade().executarCursorAtu(vAtuSeq);

						if (l != null && !l.isEmpty()) {
							obj = (Object[]) l.get(0);
							vCspSeq = (Byte) obj[0];
							vCspCnvCodigo = (Short) obj[1];
							vDtIniAtd = (Date) obj[2];
							vTamCodigo = (String) obj[3];
						}
						break;

					case H:
						// CURSOR cur_hod
						l = getInternacaoFacade().executarCursorHod(newHodSeq);

						if (l != null && !l.isEmpty()) {
							obj = (Object[]) l.get(0);
							vCspSeq = (Byte) obj[0];
							vCspCnvCodigo = (Short) obj[1];
							vDtIniAtd = (Date) obj[2];
							vTamCodigo = (String) obj[3];
						}
						break;

					default:
						break;
					}
				}
				break;
			default:
				break;
			}

			AghParametros altaObito = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO);
			vAltaObito = altaObito.getVlrTexto();

			AghParametros altaObito48 = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO_MAIS_48H);
			vAltaObito48 = altaObito48.getVlrTexto();

			if (vAltaObito.equalsIgnoreCase(vTamCodigo) || vAltaObito48.equalsIgnoreCase(vTamCodigo)) {
				vIndtipo = DominioIndTipoAltaSumarios.OBT;
			} else {
				vIndtipo = DominioIndTipoAltaSumarios.ALT;
			}

			// Busca informações do paciente
			List<AipPacientes> listaPacientes = getAipPacientesDAO().executarCursorPac(newPacCodigo);

			Date dtNasc = null;
			String nomePac = null;
			DominioSexo sexo = null;
			Integer prontuario = null;

			Integer idadeAnos = 0;
			Integer idadeMeses = 0;
			Integer idadeDias = 0;
			Date hoje = new Date();

			if (listaPacientes != null && !listaPacientes.isEmpty()) {
				nomePac = listaPacientes.get(0).getNome();  
				dtNasc = listaPacientes.get(0).getDtNascimento(); 
				sexo = listaPacientes.get(0).getSexo();
				prontuario = listaPacientes.get(0).getProntuario();

				idadeAnos = DateUtil.obterQtdAnosEntreDuasDatas(dtNasc, hoje);
				idadeMeses = DateUtil.obterQtdMesesEntreDuasDatas(dtNasc, hoje);
				idadeDias = DateUtil.obterQtdDiasEntreDuasDatas(dtNasc, hoje);
			}

			if (idadeAnos == 0 && idadeMeses == 0 & idadeDias == 0) {
				vIdadeDias = 1;
			} else {
				vIdadeDias = idadeDias;
			}
			
			AghUnidadesFuncionais unidadeFuncional = getAghuFacade().obterUnidadeFuncional(
					newUnfSeq);
			String andarAlaDescricao = unidadeFuncional.getLPADAndarAlaDescricao();

			Integer vApaSeq = getAghuFacade().executarCursorAtdPac(newAtdSeq);
			AghAtendimentoPacientes atdPac = null;
			if (vApaSeq == null) {
				atdPac = new AghAtendimentoPacientes();
				// Atualiza vApaSeq com aghc_get_agh_apa_sq1
				vApaSeq = this.getAghuFacade().obterValorSequencialIdAghAtendimentoPacientes();

				AghAtendimentoPacientesId id = new AghAtendimentoPacientesId();
				id.setAtdSeq(newAtdSeq);

				id.setSeq(vApaSeq);
				atdPac.setId(id);
				atdPac.setNumeroRn((short) 0);
				atdPac.setIndRn(false);
				atdPac.setPaciente(this.getAipPacientesDAO().obterPorChavePrimaria(newPacCodigo));
				
				getAtendimentoPacienteRN().incluirAtendimentoPaciente(atdPac);
			}
			else {
				atdPac = getAghuFacade().obterAghAtendimentoPacientesPorChavePrimaria(new AghAtendimentoPacientesId(newAtdSeq, vApaSeq));
			}

			MpmAltaSumarioId id = new MpmAltaSumarioId();
			id.setApaAtdSeq(newAtdSeq);
			id.setApaSeq(vApaSeq);
			id.setSeqp((short) 1);

			MpmAltaSumario altaSum = new MpmAltaSumario();
			altaSum.setId(id);
			altaSum.setAtendimentoPaciente(atdPac);
			altaSum.setCriadoEm(new Date());
			altaSum.setImpresso(false);
			altaSum.setTipo(vIndtipo);
			altaSum.setNome(nomePac);
			altaSum.setIdadeAnos(idadeAnos.shortValue());
			altaSum.setIdadeMeses(idadeMeses);
			altaSum.setIdadeDias(vIdadeDias);
			altaSum.setDtNascimento(dtNasc);
			altaSum.setSexo(sexo);
			altaSum.setDescUnidade(andarAlaDescricao);
			altaSum.setDthrInicio(new Date());
			altaSum.setSituacao(DominioSituacao.A);
			altaSum.setProntuario(prontuario);
			altaSum.setDthrSumarioPosAlta(new Date());
			altaSum.setPaciente(this.getAipPacientesDAO().obterPorChavePrimaria(newPacCodigo));
			altaSum.setUnidadeFuncional(this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(newUnfSeq));

			altaSum.setConvenioSaudePlano(this.getFaturamentoFacade().obterFatConvenioSaudePlano(vCspCnvCodigo, vCspSeq));
			
			altaSum.setAtendimento(this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(newAtdSeq));

			altaSum.setEspecialidade(this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(newEspSeq));

			altaSum.setServidor(this.getRegistroColaboradorFacade().obterRapServidor(new RapServidoresId(vSerMatricula,
					vSerVinCodigo)));

			altaSum.setDthrAtendimento(vDtIniAtd);
			altaSum.setConcluido(DominioIndConcluido.N);
			altaSum.setPermiteManuscrito(vIndPermiteManuscrito);
			
			try {
				this.getPrescricaoMedicaFacade().inserirAltaSumario(altaSum);
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(), e);
				throw new ApplicationBusinessException(AtendimentoEnforceRNExceptionCode.ERRO_INSERIR_ALTA_SUMARIO);
			}
		}

	}

	/**
	 * ORADB PACKAGE AGHK_ATD_RN.RN_ATDP_ATU_ALTA_SUM
	 * 
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.ExceptionAsFlowControl"})
	public void obterAltaSum(Integer newAtdSeq, DominioSituacaoSumarioAlta newIndSitSumAlta,
			DominioSituacaoSumarioAlta oldIndSitSumAlta, DominioControleSumarioAltaPendente newControleSumAlta,
			DominioOrigemAtendimento newOrigem, Integer newIntSeq, Integer newAtuSeq, Integer newHodSeq,
			Integer newPacCodigo, Short newUnfSeq, Short newEspSeq, Integer newAtdSeqMae, String nomeMicrocomputador) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		IPrescricaoMedicaFacade prescricaoMedicaFacade = this.getPrescricaoMedicaFacade();
		
		Short vSerVinCodigo;
		Integer vSerMatricula;
		Short vAsuSeqp;
		Integer vApaSeq;
		DominioPermiteManuscrito vIndPermiteManuscrito;

		try {
			if ((oldIndSitSumAlta != null && oldIndSitSumAlta.equals(DominioSituacaoSumarioAlta.P))
					&& (newIndSitSumAlta != null && newIndSitSumAlta.equals(DominioSituacaoSumarioAlta.M))) {
				if (newControleSumAlta == null) {
					vIndPermiteManuscrito = DominioPermiteManuscrito.U;
				} else {
					vIndPermiteManuscrito = DominioPermiteManuscrito.S;
				}
				vSerVinCodigo =servidorLogado.getId().getVinCodigo();
				vSerMatricula =servidorLogado.getId().getMatricula();
	
				// Cursor c_alta_sumario
				List l = prescricaoMedicaFacade.executaCursorAltaSumarioAtendimentoEnforceRN(newAtdSeq);
	
				if (l != null && !l.isEmpty()) {
					Object[] obj = (Object[]) l.get(0);
					vApaSeq = (Integer) obj[0];
					vAsuSeqp = (Short) obj[1];
					
					MpmAltaSumario altaSumario = prescricaoMedicaFacade.obterAltaSumarioPeloId(newAtdSeq, vApaSeq, vAsuSeqp);
					
					// Atualiza Alta Sumário.
					if (altaSumario != null) {
						altaSumario.setPermiteManuscrito(vIndPermiteManuscrito);
						altaSumario.setDthrSumarioPosAlta(new Date());
						altaSumario.setMatriculaConvenio(vSerMatricula.toString());
						RapServidoresId idServidorPosAlta = new RapServidoresId(vSerMatricula, vSerVinCodigo);
						altaSumario.setServidorPosAlta(getRegistroColaboradorFacade().obterRapServidor(idServidorPosAlta));
	
						try {
							this.getPrescricaoMedicaFacade().atualizarAltaSumarioViaRN(altaSumario, nomeMicrocomputador);
						} catch (ApplicationBusinessException e) {
							LOG.error(e.getMessage(), e);
							throw new ApplicationBusinessException(AtendimentoEnforceRNExceptionCode.ERRO_ATUALIZAR_ALTA_SUMARIO);
						}
					}
	
				} else {
					this.inserirAltaSumario(newOrigem, newIntSeq, newAtuSeq, newHodSeq, newAtdSeqMae, newPacCodigo,
							newUnfSeq, newAtdSeq, newEspSeq, vSerMatricula, vSerVinCodigo, vIndPermiteManuscrito);
				}
			}
	
			if ((oldIndSitSumAlta != null && oldIndSitSumAlta.equals(DominioSituacaoSumarioAlta.P))
					&& (newIndSitSumAlta != null && newIndSitSumAlta.equals(DominioSituacaoSumarioAlta.I))
					&& (newControleSumAlta != null && newControleSumAlta.equals(DominioControleSumarioAltaPendente.R))) {
				
				vSerVinCodigo =servidorLogado.getId().getVinCodigo();
				vSerMatricula =servidorLogado.getId().getMatricula();
				
				
				List<MpmAltaSumario> l = prescricaoMedicaFacade.listarAltasSumarioPorAtendimento(newAtdSeq);
	
				// Atualiza Alta Sumário.
				if (l != null && !l.isEmpty()) {
					MpmAltaSumario altaSumario = l.get(0);
					altaSumario.setDthrSumarioPosAlta(new Date());
					altaSumario.setMatriculaConvenio(vSerMatricula.toString());
					altaSumario.getServidorPosAlta().getId().setMatricula(vSerMatricula);
					altaSumario.getServidorPosAlta().getId().setVinCodigo(vSerVinCodigo);
	
					try {
						this.getPrescricaoMedicaFacade().atualizarAltaSumarioViaRN(altaSumario, nomeMicrocomputador);
					} catch (ApplicationBusinessException e) {
						LOG.error(e.getMessage(), e);
						throw new ApplicationBusinessException(AtendimentoEnforceRNExceptionCode.ERRO_ATUALIZAR_ALTA_SUMARIO);
					}
				}
				
				// Atualiza Alta Sumário.
				MpmAltaSumario altaSumario = prescricaoMedicaFacade.pesquisarAltaSumarioConcluido(newAtdSeq);
				if (altaSumario != null) {
					altaSumario.setDthrSumarioPosAlta(new Date());
					altaSumario.setMatriculaConvenio(vSerMatricula.toString());
					altaSumario.getServidorPosAlta().getId().setMatricula(vSerMatricula);
					altaSumario.getServidorPosAlta().getId().setVinCodigo(vSerVinCodigo);
	
					try {
						this.getPrescricaoMedicaFacade().atualizarAltaSumarioViaRN(altaSumario, nomeMicrocomputador);
					} catch (ApplicationBusinessException e) {
						LOG.error(e.getMessage(), e);
						throw new ApplicationBusinessException(AtendimentoEnforceRNExceptionCode.ERRO_ATUALIZAR_ALTA_SUMARIO);
					}
				}
			}
		}
		catch (InactiveModuleException e) {
			LOG.warn(e.getMessage(), e);
			getObjetosOracleDAO().atualizarSumariaAlta( newAtdSeq, newIndSitSumAlta != null ? newIndSitSumAlta.toString() : null, oldIndSitSumAlta != null ? oldIndSitSumAlta.toString() : null, 
														newControleSumAlta != null ? newControleSumAlta.toString() : null, newOrigem != null ? newOrigem.toString() : null, 
														newIntSeq, newAtuSeq, newHodSeq, newPacCodigo, newUnfSeq, newEspSeq, newAtdSeqMae, 
														servidorLogado); 
		}
	}

	/**
	 * ORADB PROCEDURE AGHP_ENFORCE_ATD_RULES
	 * 
	 * @param atendimento
	 * @param atendimentoOld
	 * @param evento
	 * @throws BaseException 
	 */
	@SuppressWarnings({"unused", "PMD.ExcessiveMethodLength"}) //enquanto método não for finalizado existirão variaveis não usadas
	public void enforceAtdRules(AghAtendimentos atendimento,
			AghAtendimentos atendimentoOld, DominioOperacoesJournal evento, String nomeMicrocomputador)
			throws BaseException {
		
		DominioTipoTratamentoAtendimento oldTipoTratamento = null;
		DominioOrigemAtendimento oldOrigem = null; 
		DominioSituacaoSumarioAlta oldIndSitSumAlta = null;
		DominioControleSumarioAltaPendente oldControleSumAlta = null; 
		Integer oldConNumero = null; 
		Integer oldProntuario = null;
		Integer oldPacCodigo = null; 
		Short oldUnfSeq = null; 
		String oldLeitoId = null; 
		Short oldQtoNum = null; 
		Integer oldAtdSeqMae = null;
		Date oldDthrInicio = null; 
		Date oldDthrFim = null; 
		Integer newSeq = null; 
		Integer newAtdSeqMae = null;
		DominioOrigemAtendimento newOrigem = null; 
		Date newDthrInicio = null; 
		Date newDthrFim = null; 
		Integer oldAtuSeq = null;
		Integer oldHodSeq = null; 
		Integer newAtuSeq = null; 
		Integer newHodSeq = null; 
		Integer newPacCodigo = null; 
		Short newUnfSeq = null;
		Short newEspSeq = null; 
		Integer newMatricula = null; 
		Short newQtoNum = null; 
		Integer newIntSeq = null; 
		Short newVinCodigo = null;
		String newLeitoId = null; 
		Integer newConNumero = null; 
		Integer newProntuario = null;
		DominioTipoTratamentoAtendimento newTipoTratamento = null; 
		DominioSituacaoSumarioAlta newIndSitSumAlta = null;
		DominioControleSumarioAltaPendente newControleSumAlta = null; 
		Date newDthrIngUnd = null;
		DominioPacAtendimento newIndPacAtendimento = null;
		DominioPacAtendimento oldIndPacAtendimento = null;
		
		if (atendimento != null) {
			newSeq = atendimento.getSeq(); 
			newAtdSeqMae = atendimento.getAtendimentoMae() == null ? null : atendimento.getAtendimentoMae().getSeq();
			newOrigem = atendimento.getOrigem(); 
			newDthrInicio = atendimento.getDthrInicio(); 
			newDthrFim = atendimento.getDthrFim(); 
			newAtuSeq = atendimento.getAtendimentoUrgencia() == null ? null : atendimento.getAtendimentoUrgencia().getSeq(); 
			newHodSeq = atendimento.getHospitalDia() == null ? null : atendimento.getHospitalDia().getSeq();
			newIndPacAtendimento = atendimento.getIndPacAtendimento();
			
			if (atendimento.getPaciente() != null) {
				newPacCodigo = atendimento.getPaciente().getCodigo(); 
				newProntuario = atendimento.getPaciente().getProntuario();
			}
			
			newUnfSeq = atendimento.getUnidadeFuncional() == null ? null : atendimento.getUnidadeFuncional().getSeq();
			newEspSeq = atendimento.getEspecialidade() == null ? null : atendimento.getEspecialidade().getSeq(); 
			
			if (atendimento.getServidor() != null) {
				newMatricula = atendimento.getServidor().getId().getMatricula(); 
				newVinCodigo = atendimento.getServidor().getId().getVinCodigo();
			}
			
			newQtoNum = atendimento.getQuarto() == null ? null : atendimento.getQuarto().getNumero();
			newIntSeq = atendimento.getInternacao() == null ? null : atendimento.getInternacao().getSeq();
			newLeitoId = atendimento.getLeito() == null ? null : atendimento.getLeito().getLeitoID();
			newConNumero = atendimento.getConsulta() == null ? null : atendimento.getConsulta().getNumero();
			newTipoTratamento = atendimento.getIndTipoTratamento();
			newIndSitSumAlta = atendimento.getIndSitSumarioAlta();
			newControleSumAlta = atendimento.getCtrlSumrAltaPendente();
			newDthrIngUnd = atendimento.getDthrIngressoUnidade();
		}
		
		if (atendimentoOld != null) {
			oldAtuSeq = atendimentoOld.getAtendimentoUrgencia() == null ? null : atendimentoOld.getAtendimentoUrgencia().getSeq();
			oldHodSeq = atendimentoOld.getHospitalDia() == null ? null : atendimentoOld.getHospitalDia().getSeq();
			oldTipoTratamento = atendimentoOld.getIndTipoTratamento();
			oldOrigem = atendimentoOld.getOrigem();
			oldIndSitSumAlta = atendimentoOld.getIndSitSumarioAlta();
			oldControleSumAlta = atendimentoOld.getCtrlSumrAltaPendente();
			oldConNumero = atendimentoOld.getConsulta() == null ? null : atendimentoOld.getConsulta().getNumero();
			oldIndPacAtendimento = atendimentoOld.getIndPacAtendimento();
			
			if (atendimentoOld.getPaciente() != null) {
				oldPacCodigo = atendimentoOld.getPaciente().getCodigo();
				oldProntuario = atendimentoOld.getPaciente().getProntuario();
			}
			
			oldUnfSeq = atendimentoOld.getUnidadeFuncional() == null ? null : atendimentoOld.getUnidadeFuncional().getSeq();
			oldLeitoId = atendimentoOld.getLeito() == null ? null : atendimentoOld.getLeito().getLeitoID();
			oldQtoNum = atendimentoOld.getQuarto() == null ? null : atendimentoOld.getQuarto().getNumero();
			oldAtdSeqMae = atendimentoOld.getAtendimentoMae() == null ? null : atendimentoOld.getAtendimentoMae().getSeq();
			oldDthrInicio = atendimentoOld.getDthrInicio();
			oldDthrFim = atendimentoOld.getDthrFim();
		}

		Boolean vUndChegagem = this.verificarUnidadeChecagem(newSeq);
		Boolean vTeste = false;
		Boolean vTemLocalizacao = false;

		if (evento == DominioOperacoesJournal.UPD) {
			/*
			 * inclui registros na tabela mpm_lista_serv_sumr_altas para os
			 * servidores relacionados a Unidade,Especialidade,Equipe e
			 * Responsavel de um atendimento
			 */
			if (!DateUtil.isDatasIguais(newDthrFim, oldDthrFim)) {
				try{
					
					this.getAtendimentosRN().atualizarListaPacientes(newOrigem, newDthrFim, newSeq, newUnfSeq, newEspSeq,
							newMatricula, newVinCodigo, newIndSitSumAlta);
					
				} catch (final InactiveModuleException e) {
					RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
					
					getObjetosOracleDAO().atualizarListaPacientes( newOrigem, newDthrFim, newSeq, 
																	newUnfSeq, newEspSeq, newMatricula, newVinCodigo, 
																	newIndSitSumAlta, 
																    servidorLogado
																 );
					LOG.warn(e.getMessage(),e);
				}
			}

			if (DominioOrigemAtendimento.I.equals(newOrigem) && vUndChegagem && oldUnfSeq != null) {
				/* se houve a troca de unidade */
				validaTrocaDeUnidade(oldUnfSeq, oldLeitoId, oldQtoNum, newSeq, newUnfSeq, newQtoNum, newLeitoId);
			}

			// Inicializa Ficha Apache (mpm_fichas_apache) se o paciente foi
			// transferido para a CTI (Ana - 01/2001)
			if (DominioOrigemAtendimento.I.equals(newOrigem)
					&& CoreUtil.modificados(newUnfSeq, oldUnfSeq)
					&& this.getLeitosInternacaoFacade().verificarCaracteristicaUnidadeFuncional(newUnfSeq,
							ConstanteAghCaractUnidFuncionais.UNID_CTI)
					&& !this.getLeitosInternacaoFacade().verificarCaracteristicaUnidadeFuncional(oldUnfSeq,
							ConstanteAghCaractUnidFuncionais.UNID_CTI)) {
				this.getAtendimentosRN().gerarDiagnosticoCti(newSeq, newUnfSeq, newDthrIngUnd);
				this.getAtendimentosRN().gerarFichaApache(newSeq, newUnfSeq, newDthrIngUnd);
				//this.rnAtdpGeraSaps3(atendimento, aghuFacade.obterUnidadeFuncional(newUnfSeq), newDthrIngUnd);
			}
			
			this.atualizarCirurgias(newOrigem, atendimento, true);

			// Atualiza situação da Ficha Apache (mpm_fichas_apache) se o
			// paciente saiu da CTI para área não CTI antes de 24 horas
			if (CoreUtil.modificados(newUnfSeq, oldUnfSeq)
					&& !this.getLeitosInternacaoFacade().verificarCaracteristicaUnidadeFuncional(newUnfSeq,
							ConstanteAghCaractUnidFuncionais.UNID_CTI)
					&& this.getLeitosInternacaoFacade().verificarCaracteristicaUnidadeFuncional(oldUnfSeq,
							ConstanteAghCaractUnidFuncionais.UNID_CTI) && this.obterNumeroHorasInternado(newSeq) < 24) {
				//this.getAtendimentosRN().atualizarFichaApache(newSeq);
				atualizarEscoreGravidadeSaps3(atendimento);
			} else {
				// Atualiza situação da Ficha Apache (mpm_fichas_apache) se o
				// paciente teve alta da CTI antes de 24 horas
				if (oldDthrFim == null
						&& newDthrFim != null
						&& this.getLeitosInternacaoFacade().verificarCaracteristicaUnidadeFuncional(newUnfSeq,
								ConstanteAghCaractUnidFuncionais.UNID_CTI)
						&& this.obterNumeroHorasInternado(newSeq) < 24) {
					//this.getAtendimentosRN().atualizarFichaApache(newSeq);
					atualizarEscoreGravidadeSaps3(atendimento);
				}
			}

			if (DominioOrigemAtendimento.I.equals(newOrigem) && CoreUtil.modificados(newUnfSeq, oldUnfSeq)) {
				this.getAtendimentosRN().atualizarUnidadeSolicitanteExame(newIntSeq, newUnfSeq);
			}

			// Grava tabela MCI_MVTO_FATOR_PREDISPONENTES
			// Qdo for recem-nascido de baixo peso
			if (DominioOrigemAtendimento.I.equals(newOrigem)
					&& CoreUtil.modificados(newUnfSeq, oldUnfSeq)
					&& this.getLeitosInternacaoFacade().verificarCaracteristicaUnidadeFuncional(newUnfSeq,
							ConstanteAghCaractUnidFuncionais.UNID_NEONATOLOGIA)) {
				this.gravarFatorPredisponenteAtendimento(atendimento, newPacCodigo, newLeitoId, newQtoNum, newUnfSeq, newDthrInicio, newDthrFim, newMatricula,
						newVinCodigo);
			}

			/* Cancela exames quando indicador for = O (óbito) */
			if(CoreUtil.modificados(newIndPacAtendimento, oldIndPacAtendimento ) ){
				this.cancelarExamesAmbulatorio(newSeq, atendimento.getIndPacAtendimento(), nomeMicrocomputador);
			}
			
			// Altera a origem da cirurgia quando o atendimento de Urgencia vira
			// uma
			// internação
			if(DominioOrigemAtendimento.I.equals(newOrigem) &&  CoreUtil.modificados(newOrigem, oldOrigem)){
				this.atualizarCirurgia(newOrigem, newSeq, newPacCodigo, newDthrInicio);
			}

			// Grava mpm_alta_sumarios quando o indicador de situação do sumário
			// é alterado de P (pendente) para M (manual) ou de P (pendente) para
			// I (informatizado) após a alta
			if (CoreUtil.modificados(newIndSitSumAlta, oldIndSitSumAlta)) {
				this.obterAltaSum(newSeq, newIndSitSumAlta, oldIndSitSumAlta, newControleSumAlta, newOrigem, newIntSeq,
						newAtuSeq, newHodSeq, newPacCodigo, newUnfSeq, newEspSeq, newAtdSeqMae, nomeMicrocomputador);
			}

			// Atualização na tabela paciente atendimentos profissionais (lista  de
			// pacientes) quando o atend. encerrar e o tipo de atendimento não exige sumário
			if (oldDthrFim == null && newIndSitSumAlta == null && DateUtil.isDatasIguais(newDthrFim, oldDthrFim)) {
				this.getAtendimentosRN().atualizarPacientesAtendProf(newSeq);
			}

			// atualização na tabela paciente atendimentos profissionais (lista
			// de pacientes) quando a situação do sumário for alterada para I
			// (informatizada) ou M (manual).
			if (CoreUtil.modificados(newIndSitSumAlta, oldIndSitSumAlta)
					&& (DominioSituacaoSumarioAlta.I.equals(newIndSitSumAlta) || DominioSituacaoSumarioAlta.M
							.equals(newIndSitSumAlta))) {
				this.getAtendimentosRN().atualizarPacientesAtendProf(newSeq);
			}

			// garantir validade do prontuário x paciente
			if (CoreUtil.modificados(newProntuario, oldProntuario)) {
				this.verificarProntuario(newPacCodigo, newProntuario);
			}

			// Verifica para atendimentos de radioterapia se existe outro no
			// período
			// Pode ser ativado para outros tratamentos, se necessário.
			if (DominioTipoTratamentoAtendimento.VALOR_28.equals(newTipoTratamento)
					&& (DateUtil.isDatasIguais(newDthrInicio, oldDthrInicio) || DateUtil.isDatasIguais(newDthrFim,
							oldDthrFim))) {
				this.getAtendimentosRN().verificarPacienteTipoTratamento(newPacCodigo, newTipoTratamento, newDthrInicio,
						newDthrFim);
			}

			if (CoreUtil.modificados(newPacCodigo, oldPacCodigo)) {
				this.atualizarCodigoPacienteProcedimentoRealizado(newSeq, newPacCodigo);
				// fatk_pmr_rn.rn_pmrp_atu_pac_cod(l_atd_row_new.seq,l_atd_row_new.pac_codigo);
			}

			// atualiza agendas e controle de dispensação das prescrição dos
			// procedimentos terapêuticos
			if (DominioOrigemAtendimento.I.equals(newOrigem) || DominioOrigemAtendimento.U.equals(newOrigem)) {
				this.getAtendimentosRN().pesquisarAtendimentosTratamentoTerapeutico("U", newPacCodigo, oldOrigem, newOrigem,
						newDthrInicio, oldDthrFim, newDthrFim, oldUnfSeq, newUnfSeq);
			}

			if (DominioOrigemAtendimento.I.equals(newOrigem) && !DominioOrigemAtendimento.I.equals(oldOrigem)) {
				this.gerarOrdemAdministracaoPrescricao(newSeq, newDthrInicio);
				// mpmp_gera_ece_quimio (l_atd_row_new.seq,
				// l_atd_row_new.dthr_inicio);
			}

			// Para leitos pesquisa 3 norte aparecerem processo limpeza
			if (DominioOrigemAtendimento.A.equals(newOrigem)
					&& (newAtuSeq == null || newIntSeq == null || newHodSeq == null) && newConNumero != null
					&& CoreUtil.modificados(newLeitoId, oldLeitoId)) {
				this.getAtendimentosRN().inserirExtratoLeito(oldLeitoId, newLeitoId);
			}
			
			if (DominioOrigemAtendimento.I.equals(newOrigem) && DominioPacAtendimento.S.equals(newIndPacAtendimento)
					&& CoreUtil.modificados(oldLeitoId, newLeitoId) && newLeitoId != null) {
				this.atualizarTransfAGHOS(newPacCodigo, oldLeitoId, newLeitoId, newProntuario, newQtoNum, newUnfSeq);
				// aghk_atd_rn_2.rn_atdp_transf_aghos(l_atd_row_new.pac_codigo,
				// l_atd_saved_row.lto_lto_id, l_atd_row_new.lto_lto_id,
				// l_atd_row_new.prontuario, l_atd_row_new.lto_lto_id,
				// l_atd_row_new.qrt_numero, l_atd_row_new.unf_seq);
			}

			if (DominioOrigemAtendimento.I.equals(newOrigem) && CoreUtil.modificados(oldDthrFim, newDthrFim)) {
				this.atualizarAltaAGHOS(newPacCodigo, oldDthrFim, newDthrFim, newProntuario, newLeitoId, newQtoNum, newUnfSeq);
				// aghk_atd_rn_2.rn_atdp_alta_aghos(l_atd_row_new.pac_codigo,
				// l_atd_saved_row.dthr_fim, l_atd_row_new.dthr_fim,
				// l_atd_row_new.prontuario, l_atd_row_new.lto_lto_id,
				// l_atd_row_new.qrt_numero, l_atd_row_new.unf_seq);
			}
			
		} else if (evento == DominioOperacoesJournal.INS) {
			// garantir validade do prontuário x paciente
			if (newProntuario != null) {
				verificarProntuario(newPacCodigo, newProntuario);
			}

			// Grava tabela MCI_MVTO_FATOR_PREDISPONENTES. Qdo for recem-nascido
			// de baixo peso
			if (this.getLeitosInternacaoFacade().verificarCaracteristicaUnidadeFuncional(newUnfSeq,
					ConstanteAghCaractUnidFuncionais.UNID_UTIN)) {
				this.gravarFatorPredisponenteAtendimento(atendimento, newPacCodigo, newLeitoId, newQtoNum, newUnfSeq, newDthrInicio, newDthrFim, newMatricula, newVinCodigo);
				// AGHK_ATD_RN.RN_ATDP_GRAVA_MFP
				// ( l_atd_row_new.seq,
				// l_atd_row_new.pac_codigo,
				// l_atd_row_new.lto_lto_id,
				// l_atd_row_new.qrt_numero,
				// l_atd_row_new.unf_seq,
				// l_atd_row_new.dthr_inicio,
				// l_atd_row_new.dthr_fim,
				// l_atd_row_new.ser_matricula,
				// l_atd_row_new.ser_vin_codigo);
			}

			// Inicializa Ficha Apache (mpm_fichas_apache) se o paciente foi
			// internado na CTI
			if (DominioOrigemAtendimento.I.equals(newOrigem)
					&& this.getLeitosInternacaoFacade().verificarCaracteristicaUnidadeFuncional(newUnfSeq,
							ConstanteAghCaractUnidFuncionais.UNID_CTI)) {
				this.getAtendimentosRN().gerarDiagnosticoCti(newSeq, newUnfSeq, newDthrIngUnd);
				this.getAtendimentosRN().gerarFichaApache(newSeq, newUnfSeq, newDthrIngUnd);
				//this.rnAtdpGeraSaps3(atendimento, aghuFacade.obterUnidadeFuncional(newUnfSeq), newDthrIngUnd);
			}
			
			this.atualizarCirurgias(newOrigem, atendimento, false);

			// atualiza agendas e controle de dispensação das prescrição dos
			// procedimentos terapêuticos
			if (DominioOrigemAtendimento.I.equals(newOrigem) || DominioOrigemAtendimento.U.equals(newOrigem)) {
				this.getAtendimentosRN().pesquisarAtendimentosTratamentoTerapeutico("I", newPacCodigo, null, newOrigem,
						newDthrInicio, null, newDthrFim, null, newUnfSeq);
			}

			if (DominioOrigemAtendimento.I.equals(newOrigem)) {
				this.gerarOrdemAdministracaoPrescricao(newSeq, newDthrInicio);
				// mpmp_gera_ece_quimio (l_atd_row_new.seq,
				// l_atd_row_new.dthr_inicio);
			}

			// atualiza as informações do PIM2 (escore de gravidade da Utip)
			atualizarInformacoesFormularioPim2("I", newOrigem, newSeq, null, newUnfSeq, null, newDthrFim, newDthrIngUnd);

		} else if (evento == DominioOperacoesJournal.DEL) {
			// Atualiza agendas e controle de dispensação das prescrição dos
			// procedimentos terapêuticos
			if (DominioOrigemAtendimento.I.equals(oldOrigem) || DominioOrigemAtendimento.U.equals(oldOrigem)) {
				this.getAtendimentosRN().pesquisarAtendimentosTratamentoTerapeutico("D", oldPacCodigo, oldOrigem, null,
						oldDthrInicio, oldDthrFim, null, oldUnfSeq, null);
			}
		}

	}
	
	private void validaTrocaDeUnidade(Short oldUnfSeq, String oldLeitoId, Short oldQtoNum,
			Integer newSeq, Short newUnfSeq, Short newQtoNum, String newLeitoId)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final Date ontem = DateUtil.truncaData(DateUtil.adicionaDias(new Date(), -1));
		if (CoreUtil.modificados(newUnfSeq, oldUnfSeq)) {
			if( ! this.existeLocalizacao(newSeq, ontem,newUnfSeq)) {
				try{
					this.inserirOrdemLocalizacao(newSeq, ontem, newUnfSeq, newQtoNum, newLeitoId);
					
				} catch (final InactiveModuleException e) {
					getObjetosOracleDAO().inserirOrdemLocalizacao( newSeq, ontem, newUnfSeq, newQtoNum, newLeitoId, servidorLogado);
					LOG.warn(e.getMessage(),e);
				}
			} else {
				try{
					this.alterarOrdemLocalizacao(newSeq, ontem, oldUnfSeq, newUnfSeq, newQtoNum, newLeitoId);
					
				} catch (final InactiveModuleException e) {
					getObjetosOracleDAO().alteraOrdemLocalizacao( newSeq, ontem, oldUnfSeq, newUnfSeq, newQtoNum, newLeitoId, servidorLogado);
					LOG.warn(e.getMessage(),e);
				}
			}
		} else if (CoreUtil.modificados(newLeitoId, oldLeitoId) || CoreUtil.modificados(newQtoNum, oldQtoNum)) {
			try {
				this.alterarOrdemLocalizacao(newSeq, ontem, newUnfSeq, newUnfSeq, newQtoNum, newLeitoId);
			} catch (final InactiveModuleException e) {
				getObjetosOracleDAO().alteraOrdemLocalizacao(newSeq, ontem, newUnfSeq, newUnfSeq, newQtoNum, newLeitoId, servidorLogado);
				LOG.warn(e.getMessage(),e);
			}
		}
	}

	/**
	 * ORADB AGHK_ATD_RN.RN_ATDP_ATU_CIRURGIA
	 * Método que atualiza as cirurgias do dia seguinte e do dia atual do paciente, setando
	 * o atendimento e a origem como 'I'
	 * @throws MECBaseException 
	 */
	private void atualizarCirurgias(DominioOrigemAtendimento newOrigem, AghAtendimentos atendimento, boolean atualizarAtendimentoEOrigemPacCirgDaCirurgia) throws BaseException {
		if (DominioOrigemAtendimento.I.equals(newOrigem)){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			List<MbcCirurgias> listaCirurgias = new ArrayList<MbcCirurgias>();
			
			listaCirurgias.addAll(blocoCirurgicoFacade.pesquisarCirurgiasPacienteDataAtualEDiaSeguinte(atendimento.getPaciente()));
			
			// #36162 - Vincular atendimento de internação na cirurgia - problema dos pacientes que internam no sábado pelo AGHU
			List<MbcCirurgias> listaCirurgiasEntDias = blocoCirurgicoFacade.pesquisarCirurgiasPacienteDataEntreDias(atendimento.getPaciente());
			
			for (MbcCirurgias cirurgiaEntDias: listaCirurgiasEntDias){
				
				if (blocoCirurgicoFacade.verificaExistenciaPeviaDefinitivaPorUNFData(cirurgiaEntDias.getUnidadeFuncional().getSeq(), 
															cirurgiaEntDias.getData(),DominioTipoEscala.D)){
					listaCirurgias.add(cirurgiaEntDias);
				}
			}
			
			for (MbcCirurgias cirurgia: listaCirurgias){
				cirurgia.setAtendimento(atendimento);
				cirurgia.setOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia.I);
				
				// Correção desenvolvida para resolver o Incidente - AGHU #52096
				// Substituindo a chamada do persistirCirurgia(cirurgia) nos dois casos, quando foi feita para corrigir o Incidente - AGHU #51975
				if(atualizarAtendimentoEOrigemPacCirgDaCirurgia){
					blocoCirurgicoFacade.atualizarOrigemPacienteEAtendimentoDaCirurgia(cirurgia);
				} else {
					blocoCirurgicoFacade.persistirCirurgia(cirurgia, servidorLogado);
				}
			}
		}
	}

	/**
	 * @ORADB AGHK_ATD_RN_2.RN_ATDP_ALTA_AGHOS
	 * 
	 * @param newPacCodigo
	 * @param oldDthrFim
	 * @param newDthrFim
	 * @param newProntuario
	 * @param newLeitoId
	 * @param newQtoNum
	 * @param newUnfSeq
	 *  
	 */
	private void atualizarAltaAGHOS(Integer newPacCodigo, Date oldDthrFim, Date newDthrFim, Integer newProntuario, String newLeitoId, Short newQtoNum,
			Short newUnfSeq) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.getObjetosOracleDAO().atualizarAltaAGHOS(newPacCodigo, oldDthrFim, newDthrFim, newProntuario, newLeitoId, newQtoNum, newUnfSeq, servidorLogado);
	}

	/**
	 * @ORADB AGHK_ATD_RN_2.RN_ATDP_TRANSF_AGHOS
	 * 
	 * @param newPacCodigo
	 * @param oldLeitoId
	 * @param newLeitoId
	 * @param newProntuario
	 * @param newQtoNum
	 * @param newUnfSeq
	 *  
	 */
	private void atualizarTransfAGHOS(Integer newPacCodigo, String oldLeitoId, String newLeitoId, Integer newProntuario, Short newQtoNum, Short newUnfSeq) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.getObjetosOracleDAO().atualizarTransfAGHOS(newPacCodigo, oldLeitoId, newLeitoId, newProntuario, newQtoNum, newUnfSeq, servidorLogado);
	}

	/**
	 * @ORADB MPMP_GERA_ECE_QUIMIO
	 * 
	 * @param newSeq
	 * @param newDthrInicio
	 *  
	 */
	private void gerarOrdemAdministracaoPrescricao(Integer newSeq,
			Date newDthrInicio) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.getObjetosOracleDAO().gerarOrdemAdministracaoPrescricao(newSeq, newDthrInicio, servidorLogado);
	}

	/**
	 * @ORADB fatk_pmr_rn.rn_pmrp_atu_pac_cod
	 * 
	 * @param newSeq
	 * @param newPacCodigo
	 * @throws ApplicationBusinessException
	 */
	private void atualizarCodigoPacienteProcedimentoRealizado(final Integer newSeq,
			final Integer newPacCodigo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.getObjetosOracleDAO().atualizarCodigoPacienteProcedimentoRealizado(newSeq, newPacCodigo, servidorLogado);
	}

	/**
	 * 
	 * @ORADB AGHK_ATD_RN.RN_ATDP_ATU_CIR_UPD
	 * 
	 * @param newOrigem
	 * @param newSeq
	 * @param newPacCodigo
	 * @param newDthrInicio
	 * @throws MECBaseException 
	 */
	private void atualizarCirurgia(final DominioOrigemAtendimento newOrigem, final Integer newSeq, final Integer newPacCodigo, final Date newDthrInicio) throws BaseException {
		atualizaCirurgiaNoAtendimento(newOrigem, newSeq, newPacCodigo, newDthrInicio);
	}
	
	private void atualizaCirurgiaNoAtendimento(final DominioOrigemAtendimento newOrigem, final Integer newSeq, final Integer newPacCodigo, final Date newDthrInicio) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(DominioOrigemAtendimento.I.equals(newOrigem)){
			MbcCirurgias cir = populaCirurgia(newSeq, newPacCodigo, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
			
			List<MbcCirurgias> cirurgias = blocoCirurgicoFacade.pesquisaCirurgiasPorCriterios( cir.getData(), cir.getDigitaNotaSala(), 
																							   cir.getPaciente().getCodigo(), 
																							   cir.getAtendimento().getSeq(), 
																							   cir.getOrigemPacienteCirurgia());
			for(MbcCirurgias cirurgia : cirurgias){
				cirurgia.setOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia.I);
				cir.setServidor(servidorLogado);
				blocoCirurgicoFacade.persistirCirurgia(cirurgia, servidorLogado);
			}

			cir = populaCirurgia(newSeq, newPacCodigo, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
			
			cirurgias = blocoCirurgicoFacade.pesquisaCirurgiasPorCriterios( cir.getData(), cir.getDigitaNotaSala(), 
																			cir.getPaciente() != null ? cir.getPaciente().getCodigo() : null, 
																			cir.getAtendimento() != null ? cir.getAtendimento().getSeq() : null, 
																			cir.getOrigemPacienteCirurgia());
			for(MbcCirurgias cirurgia : cirurgias){
				AghAtendimentos atendimento = new AghAtendimentos(newSeq);
				cir.setAtendimento(atendimento);
				cir.setServidor(servidorLogado);
				blocoCirurgicoFacade.persistirCirurgia(cirurgia, servidorLogado);
			}
		}
	}
	
	/**
	 * Popula cirurgia para consulta 
	 * @param newSeq
	 * @param newPacCodigo
	 * @param isAtendimentoNull
	 * @param isOrigemPacienteNull
	 * @return
	 */
	private MbcCirurgias populaCirurgia(final Integer newSeq, final Integer newPacCodigo, Boolean isAtendimentoNull, Boolean isOrigemPacienteNull, Boolean isDateNull) {
		MbcCirurgias cir = new MbcCirurgias();
		cir.setDigitaNotaSala(Boolean.FALSE);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(newPacCodigo);
		cir.setPaciente(paciente);
		
		if(!isAtendimentoNull){
			AghAtendimentos atendimento = new AghAtendimentos(newSeq);
			cir.setAtendimento(atendimento);
		} 
		
		if(!isOrigemPacienteNull){
			cir.setOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia.I);
		}	
		
		if(!isDateNull){
			cir.setData(new Date());
		}
		
		return cir;
	}

	private void cancelarExamesAmbulatorio(Integer newSeq,
			DominioPacAtendimento indPacAtendimento, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		try{
			this.getAtendimentosRN().cancelarExamesNaoRealizados(newSeq, indPacAtendimento, nomeMicrocomputador);
			
		} catch (final InactiveModuleException e) {
			getObjetosOracleDAO().cancelarExamesAmbulatorio(newSeq, indPacAtendimento, servidorLogado);
			LOG.warn(e.getMessage(),e);
		}
	}

	/*
	 * @ORADB : RN_ATDP_GERA_SAPS3 
	 */
	protected void rnAtdpGeraSaps3(AghAtendimentos atendimento, AghUnidadesFuncionais unidade, Date dtIngressoUnidade) {
		MpmEscoreSaps3 sap = new MpmEscoreSaps3();
		sap.setAtendimento(atendimento);
		sap.setUnidade(unidade);
		sap.setDthrIngressoUnidade(dtIngressoUnidade);
		sap.setPaciente(atendimento.getPaciente());
		prescricaoMedicaFacade.persistir(sap);
	}

	protected void atualizarEscoreGravidadeSaps3(AghAtendimentos atendimento) {
		List<MpmEscoreSaps3> lista = prescricaoMedicaFacade.pesquisarEscorePendentePorAtendimento(atendimento.getSeq());
		if(lista != null && !lista.isEmpty()) {
			for(MpmEscoreSaps3 sap : lista) {
				sap.setIndSituacao(DominioSituacaoSaps3.A);
				prescricaoMedicaFacade.persistir(sap);
			}
		}
	}

/**
 * 
 * @ORADB AGHK_ATD_RN.RN_ATDP_GRAVA_MFP
 * @
 * @param atendimento
 * @param newPacCodigo
 * @param newLeitoId
 * @param newQtoNum
 * @param newUnfSeq
 * @param newDthrInicio
 * @param newDthrFim
 * @param newMatricula
 * @param newVinCodigo
 *  
 */
	public void gravarFatorPredisponenteAtendimento(AghAtendimentos atendimento, Integer newPacCodigo, String newLeitoId, Short newQtoNum, Short newUnfSeq,
			Date newDthrInicio, Date newDthrFim, Integer newMatricula, Short newVinCodigo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.getObjetosOracleDAO().inserirMvtoFatorPredisponente(
				atendimento.getSeq(), newPacCodigo, newLeitoId, newQtoNum,
				newUnfSeq, newDthrInicio, newDthrFim, newMatricula,
				newVinCodigo,
				servidorLogado);

		/* TODO Código retirado pois as triggers da entidade MciMvtoFatorPredisponentes não foram implementadas. 
		 * Será realizado chamada direto ao Oracle, quando possível.
		 *
		ICadastroPacienteFacade cadastroPacienteFacade = getCadastroPacienteFacade();
		List<AipPesoPacientes> aipPesoPacientes = cadastroPacienteFacade.listarPesosPaciente(newPacCodigo, DominioMomento.N);
		if (aipPesoPacientes != null && !aipPesoPacientes.isEmpty()) {

			final List<MciMvtoFatorPredisponentes> listarMvtoFatorPredisponentes = this.getControleInfeccaoFacade().listarMvtoFatorPredisponentes(
					atendimento.getSeq());
			if (listarMvtoFatorPredisponentes != null && !listarMvtoFatorPredisponentes.isEmpty()) {
				// TODO Pode retornar + de 1 registro. O primeiro é o que deve ser usado???
				AipPesoPacientes aipPesoPaciente = aipPesoPacientes.get(0);
				MciFatorPredisponentes fatorPredisponentes = cadastroPacienteFacade.obterFatorPredisponentesPorPeso(aipPesoPaciente.getPeso());
				if (fatorPredisponentes != null) {
					MciMvtoFatorPredisponentes mciMvtoFatorPredisponentes = new MciMvtoFatorPredisponentes();
					mciMvtoFatorPredisponentes.setAtendimento(atendimento);
					mciMvtoFatorPredisponentes.setPaciente(this.getAipPacientesDAO().obterPorChavePrimaria(newPacCodigo));
					mciMvtoFatorPredisponentes.setLtoLtoId(newLeitoId);
					mciMvtoFatorPredisponentes.setQrtNumero(newQtoNum);
					mciMvtoFatorPredisponentes.setUnfSeq(newUnfSeq);
					mciMvtoFatorPredisponentes.setDataInicio(newDthrInicio);
					mciMvtoFatorPredisponentes.setDataFim(newDthrFim);
					mciMvtoFatorPredisponentes.setSerMatricula(newMatricula);
					mciMvtoFatorPredisponentes.setSerVinCodigo(newVinCodigo);

					this.getInternacaoFacade().inserirMvtoFatorPredisponente(mciMvtoFatorPredisponentes);
					
				}
			}
		}
		 */
	}

	private void alterarOrdemLocalizacao(Integer newSeq, Date ontem,
			Short oldUnfSeq, Short newUnfSeq, Short newQtoNum, String newLeitoId) throws ApplicationBusinessException {
		this.getChecagemEletronicaFacade().alterarOrdemLocalizacao(newSeq, ontem, newUnfSeq, newQtoNum, newLeitoId);
		
	}

	private void inserirOrdemLocalizacao(Integer newSeq, Date ontem,
			Short newUnfSeq, Short newQtoNum, String newLeitoId) throws ApplicationBusinessException {
		this.getChecagemEletronicaFacade().inserirOrdemLocalizacao(newSeq, ontem, newUnfSeq, newQtoNum, newLeitoId);
		
	}

	private boolean existeLocalizacao(Integer newSeq, Date truncaData,
			Short newUnfSeq) {
		return getChecagemEletronicaFacade().existeLocalizacao(newSeq, truncaData, newUnfSeq);
	}

	/**
	 * Chegar unidades.
	 * 
	 * ORADB FUNCTION MPMC_UNIDADE_CHECAGEM
	 * 
	 * @param atdSeq
	 * @return
	 */
	public Boolean verificarUnidadeChecagem(Integer atdSeq) {
		// Alterado em 24/03/2009 por Ney (AGH) para gerar informações de
		// checagem para todas unidades.
		// Migração não trouxe lógica.
		return true;
	}

	/**
	 * ORADB PROCEDURE RN_LCP_ATU_LOCAL SUB PROCEDURE verifica_prontuario
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	private void verificarProntuario(Integer pacCod, Integer prontuario) throws ApplicationBusinessException{
		Integer pacAux = null;

		List<Integer> l = getAipPacientesDAO().executarCursorPaciente(prontuario);

		// Atualiza Alta Sumário.
		if (l != null && !l.isEmpty()) {
			pacAux = l.get(0);
		}

		if(pacAux != null && !pacAux.equals(pacCod)) {
			throw new ApplicationBusinessException(AtendimentoEnforceRNExceptionCode.PRONT_NAO_PERTENCE, prontuario,
					pacCod);
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
		
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected ILeitosInternacaoFacade getLeitosInternacaoFacade() {
		return this.leitosInternacaoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IChecagemEletronicaFacade getChecagemEletronicaFacade() {
		return checagemEletronicaFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade(){
		return blocoCirurgicoFacade;
	}
	
	protected AtendimentoPacienteRN getAtendimentoPacienteRN() {
		return atendimentoPacienteRN;
	}

	protected AtendimentosRN getAtendimentosRN() {
		return atendimentosRN;
	}

	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}
	
	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
