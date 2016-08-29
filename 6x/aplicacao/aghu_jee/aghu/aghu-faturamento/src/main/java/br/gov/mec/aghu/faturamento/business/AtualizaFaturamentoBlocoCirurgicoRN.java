package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobranca;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudePlanoDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatLogInterfaceDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatLogInterface;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * ORADB PROCEDURE FATP_ATU_FAT_MBC
 * 
 * @author aghu
 * 
 */
@Stateless
public class AtualizaFaturamentoBlocoCirurgicoRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -633536088641367733L;


	@EJB
	private FatkCthRN fatkCthRN;
	
	@EJB
	private ProcedimentosAmbRealizadoRN procedimentosAmbRealizadoRN;
	
	@EJB
	private VerificacaoFaturamentoSusRN verificacaoFaturamentoSusRN;
	
	@EJB
	private ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON;
	
	@EJB
	private ItemContaHospitalarON itemContaHospitalarON;
	
	private static final Log LOG = LogFactory.getLog(AtualizaFaturamentoBlocoCirurgicoRN.class);	
	
	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	
	@Inject
	private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;
	
	@Inject
	private FatLogInterfaceDAO fatLogInterfaceDAO;
	
	@Inject
	private FatConvenioSaudePlanoDAO fatConvenioSaudePlanoDAO;
	
	@Inject
	private VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO;


	public enum AtualizaFaturamentoBlocoCirurgicoRNExceptionCode implements BusinessExceptionCode {
		FAT_00518, FAT_00521, FAT_00620, FAT_00621;
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	
	/**
	 * ORADB PROCEDURE FATP_ATU_FAT_MBC
	 * <p>
	 * Atualiza o faturamento do bloco cirurgico
	 * </p>
	 * 
	 * @param procEspPorCirurgias
	 * 
	 * @throws BaseException
	 */
	public void atualizarFaturamentoBlocoCirurgico(MbcProcEspPorCirurgias procEspPorCirurgia, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		// Variáveis locais
		MbcCirurgias cirurgia = procEspPorCirurgia.getCirurgia();
		FatConvenioSaude plano = cirurgia.getConvenioSaude();
		DominioGrupoConvenio grupoConvenio = plano.getGrupoConvenio();
		FatConvenioSaudePlano valorConvenio = null;
		Short valorQuantidade = procEspPorCirurgia.getQtd().shortValue();
		// Mensagem de interface
		StringBuffer valorMensagem = new StringBuffer(640);

		if (DominioGrupoConvenio.S.equals(grupoConvenio)) { // Se convenio for SUS, executa a interface
			// Busca PHI correpondente ao código de procedimento cirúrgico
			FatProcedHospInternos valorProcedimentoHospitalarInterno = this.getValorProcedimentoHospitalarInterno(procEspPorCirurgia);

			MbcProcedimentoCirurgicos procedimentoCirurgico = procEspPorCirurgia.getProcedimentoCirurgico();
			Boolean isLancaItemAmbulatorio = procedimentoCirurgico.getIndLancaAmbulatorio();

			DominioOrigemPacienteCirurgia valorOrigemAtendimento = cirurgia.getOrigemPacienteCirurgia();
			valorMensagem.append("ORIG=" + valorOrigemAtendimento + " ");

			// Verifica se a especialidade tem a característica de LANÇAR ITEM AMBULATÓRIO
			final Boolean isEspecialidadeLancaItemAmbulatorio = getInternacaoFacade().verificarCaracteristicaEspecialidade(
					procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getId().getEspSeq(), DominioCaracEspecialidade.LANCA_ITEM_AMBULATORIO);

			if (isEspecialidadeLancaItemAmbulatorio != null && isEspecialidadeLancaItemAmbulatorio && isLancaItemAmbulatorio != null && isLancaItemAmbulatorio && DominioOrigemPacienteCirurgia.I.equals(valorOrigemAtendimento)) {

				List<VFatAssociacaoProcedimento> listaFatAssociacaoProcedimento = getVFatAssociacaoProcedimentoDAO().pesquisarVFatTipoGrupoContaSusPorCnvCspPhi(
						cirurgia.getConvenioSaudePlano().getId().getCnvCodigo(), cirurgia.getConvenioSaudePlano().getId().getSeq(), valorProcedimentoHospitalarInterno.getSeq());

				if (listaFatAssociacaoProcedimento.isEmpty()) {

					// Obtém o convênio SUS padrão
					AghParametros parametroConvenioSusPadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
					Short valorConvenioSusPadrao = parametroConvenioSusPadrao.getVlrNumerico().shortValue();

					// Obtém o plano do SUS no ambulatório
					AghParametros parametroSusPlanoAmbulatorio = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
					Byte valorSusPlanoAmbulatorio = parametroSusPlanoAmbulatorio.getVlrNumerico().byteValue();

					valorConvenio = this.getFatConvenioSaudePlanoDAO().obterPorChavePrimaria(new FatConvenioSaudePlanoId(valorConvenioSusPadrao, valorSusPlanoAmbulatorio));
					plano = valorConvenio.getConvenioSaude();
					valorOrigemAtendimento = DominioOrigemPacienteCirurgia.A;

				} else {
					// A cirurgia é responsável por informar o convênio e origem do atendimento
					plano = cirurgia.getConvenioSaude();
					valorOrigemAtendimento = cirurgia.getOrigemPacienteCirurgia();
				}
			} else {
				// A cirurgia é responsável por informar o convênio e origem do atendimento
				plano = cirurgia.getConvenioSaude();
				valorOrigemAtendimento = cirurgia.getOrigemPacienteCirurgia();
			}

			// FLAG que controla a ocorrência de atualização do procedimento realizado no ambulatório
			boolean isSqlAtualizouProcedimentoAmbulatorio = false;

			// Cobrança em AMBULATÓRIO
			if (DominioOrigemPacienteCirurgia.A.equals(valorOrigemAtendimento)) {
				// Se a situacao ATIVO, atualiza COMPETÊNCIA (CPE)
				if (DominioSituacao.A.equals(procEspPorCirurgia.getSituacao())) {
					// Obtém a competência do AMBULATÓRIO
					List<FatCompetencia> listaValorOk = this.getVerificacaoFaturamentoSusRN().obterCompetenciasAbertasNaoFaturadasPorModulo(DominioModuloCompetencia.AMB);
					FatCompetencia valorOk = listaValorOk.get(0);

					// Busca dados da competência aberta
					List<FatProcedAmbRealizado> listaProcedAmbRealizado = this.getFatProcedAmbRealizadoDAO().listarFatProcedAmbRealizadoComCompetenciaAberta(cirurgia.getSeq(),
							procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getId().getEspSeq(), procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getId().getPciSeq(),
							procEspPorCirurgia.getId().getIndRespProc());

					for (FatProcedAmbRealizado procedAmbRealizado : listaProcedAmbRealizado) {
						// Situação
						procedAmbRealizado.setSituacao(this.getSituacaoProcedimentoAmbulatorio(procEspPorCirurgia.getSituacao()));
						if (procEspPorCirurgia.getQtd() != null) {
							// Quantidade
							procedAmbRealizado.setQuantidade(procEspPorCirurgia.getQtd().shortValue());
						}
						procedAmbRealizado.setFatCompetencia(valorOk); // Competência
						procedAmbRealizado.setProcedimentoHospitalarInterno(procEspPorCirurgia.getProcedHospInterno()); // Procedimento Hospitalar Interno
						procedAmbRealizado.setCid(procEspPorCirurgia.getCid()); // CID
						// ATUALIZA procedimento realizado no ambulatório
						this.getProcedimentosAmbRealizadoRN().atualizar(procedAmbRealizado);
						isSqlAtualizouProcedimentoAmbulatorio = true; // Executou
					}
				} else { // NÃO atualiza COMPETÊNCIA (CPE)

					// Busca dados SEM COMPETÊNCIA
					List<FatProcedAmbRealizado> listaProcedAmbRealizado = this.getFatProcedAmbRealizadoDAO().listarFatProcedAmbRealizadoSemCompetenciaAberta(cirurgia.getSeq(),
							procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getId().getEspSeq(), procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getId().getPciSeq(),
							procEspPorCirurgia.getId().getIndRespProc());

					for (FatProcedAmbRealizado procedAmbRealizado : listaProcedAmbRealizado) {

						// Situação
						procedAmbRealizado.setSituacao(this.getSituacaoProcedimentoAmbulatorio(procEspPorCirurgia.getSituacao()));
						procedAmbRealizado.setQuantidade(valorQuantidade); // Quantidade
						procedAmbRealizado.setProcedimentoHospitalarInterno(valorProcedimentoHospitalarInterno); // Procedimento Hospitalar Interno
						procedAmbRealizado.setCid(procEspPorCirurgia.getCid()); // CID

						// ATUALIZA procedimento realizado no ambulatório
						this.getProcedimentosAmbRealizadoRN().atualizar(procedAmbRealizado);
						isSqlAtualizouProcedimentoAmbulatorio = true; // Executou
					}
				}

				// Quando NÃO ATUALIZOU o procedimento realizado no ambulatório
				if (!isSqlAtualizouProcedimentoAmbulatorio) {

					// Quando situação for DIFERENTE DE CANCELADA
					if (!DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao())) {

						valorMensagem.append("INS PMR ");

						FatProcedAmbRealizado procedAmbRealizado = new FatProcedAmbRealizado();

						procedAmbRealizado.setDthrRealizado(cirurgia.getDataInicioCirurgia());
						procedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
						procedAmbRealizado.setLocalCobranca(DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B);
						procedAmbRealizado.setQuantidade(valorQuantidade);
						procedAmbRealizado.setProcedimentoHospitalarInterno(valorProcedimentoHospitalarInterno); // Seta de MbcProcEspPorCirurgias
						procedAmbRealizado.setUnidadeFuncional(cirurgia.getUnidadeFuncional());
						procedAmbRealizado.setEspecialidade(cirurgia.getEspecialidade());
						procedAmbRealizado.setPaciente(cirurgia.getPaciente());
						procedAmbRealizado.setConvenioSaudePlano(valorConvenio!=null?valorConvenio:cirurgia.getConvenioSaudePlano());
						procedAmbRealizado.setIndOrigem(DominioOrigemProcedimentoAmbulatorialRealizado.CIA); // Cirurgia Ambulatório
						procedAmbRealizado.setProcEspPorCirurgia(procEspPorCirurgia);
						procedAmbRealizado.setAtendimento(cirurgia.getAtendimento());
						procedAmbRealizado.setCid(procEspPorCirurgia.getCid());
						
						// procedAmbRealizado.setConvenioSaudePlano(cirurgia.getConvenioSaudePlano());

						// #36553
						procedAmbRealizado.setPpcCrgSeq(procEspPorCirurgia.getCirurgia().getSeq());
						procedAmbRealizado.setPpcEprPciSeq(procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getId().getPciSeq());
						procedAmbRealizado.setPpcEprEspSeq(procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getId().getEspSeq());
						procedAmbRealizado.setPpcIndRespProc(procEspPorCirurgia.getId().getIndRespProc());
						
						// INSERE procedimento realizado no ambulatório
						this.getProcedimentosAmbRealizadosON().inserirFatProcedAmbRealizado(procedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
					} else {
						valorMensagem.append("SIT CRG=CANC ");
					}

				} else {
					valorMensagem.append("UPD PMR DECODE('" + procEspPorCirurgia.getSituacao() + "') ");
				}
			} else if (DominioOrigemPacienteCirurgia.I.equals(valorOrigemAtendimento)) {

				// Busca dados SEM COMPETÊNCIA
				List<FatProcedAmbRealizado> listaProcedAmbRealizado = this.getFatProcedAmbRealizadoDAO().listarFatProcedAmbRealizadoSemCompetenciaAberta(cirurgia.getSeq(),
						procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getId().getEspSeq(), procEspPorCirurgia.getMbcEspecialidadeProcCirgs().getId().getPciSeq(),
						procEspPorCirurgia.getId().getIndRespProc());

				for (FatProcedAmbRealizado procedAmbRealizado : listaProcedAmbRealizado) {

					// Seta situação para CANCELADO
					procedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);

					// ATUALIZA procedimento realizado no ambulatório
					this.getProcedimentosAmbRealizadoRN().atualizar(procedAmbRealizado);
					isSqlAtualizouProcedimentoAmbulatorio = true; // Executou
				}

				// Cobrança em INTERNAÇÃO
				final Integer valorContaHospitalar = this.getFatkCthRN().rnCthcVerCtaPac(cirurgia.getPaciente().getCodigo(), cirurgia.getDataInicioCirurgia());
				valorMensagem.append("CTH_SEQ='" + valorContaHospitalar + "' ");

				if (CoreUtil.maior(valorContaHospitalar, 0)) {

					// Pesquisa somente conta se esta ainda NÃO FOI FATURADA
					FatContasHospitalares contaHospitalar = this.getFatContasHospitalaresDAO().obterPorChavePrimaria(valorContaHospitalar);
					
					if(contaHospitalar!=null) {
						this.getFatContasHospitalaresDAO().refresh(contaHospitalar);
					}

					// Se a conta estiver aberta ou fechada
					if (contaHospitalar != null && CoreUtil.in(contaHospitalar.getIndSituacao(), DominioSituacaoConta.A, DominioSituacaoConta.F)
							&& contaHospitalar.getMotivoSaidaPaciente() == null) {

						// FLAG que controla a ocorrência de atualização no item de conta hospitalar
						boolean isSqlAtualizouFatItemContaHospitalar = false;

						List<FatItemContaHospitalar> listaItemContaHospitalar = this.getFatItemContaHospitalar().listarContaHospitalarPorProcEspPorCirurgias(procEspPorCirurgia);

						for (FatItemContaHospitalar itemContaHospitalar : listaItemContaHospitalar) {

							DominioSituacaoItenConta situacaoItenConta = DominioSituacao.I.equals(procEspPorCirurgia.getSituacao()) ? DominioSituacaoItenConta.C
									: DominioSituacaoItenConta.A;
							itemContaHospitalar.setIndSituacao(situacaoItenConta);
							itemContaHospitalar.setQuantidade(valorQuantidade);
							itemContaHospitalar.setProcedimentoHospitalarInterno(valorProcedimentoHospitalarInterno);

							isSqlAtualizouFatItemContaHospitalar = true;
						}

						// Quando NÃO ATUALIZOU item de conta hospitalar
						if (!isSqlAtualizouFatItemContaHospitalar) {

							// Quando situação for DIFERENTE DE CANCELADA
							if (!DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao())) {

								valorMensagem.append("INS ICH ");

								FatItemContaHospitalarId id =  new FatItemContaHospitalarId();
								id.setCthSeq(valorContaHospitalar);
								id.setSeq(getFatItemContaHospitalar().obterProximoSeq(valorContaHospitalar));
								
								FatItemContaHospitalar itemContaHospitalar = new FatItemContaHospitalar();
								itemContaHospitalar.setId(id);
								itemContaHospitalar.setContaHospitalar(contaHospitalar);
								itemContaHospitalar.setIchType(DominioItemConsultoriaSumarios.ICH);
								itemContaHospitalar.setProcedimentoHospitalarInterno(valorProcedimentoHospitalarInterno);
								itemContaHospitalar.setIndSituacao(DominioSituacaoItenConta.A);
								itemContaHospitalar.setQuantidadeRealizada(valorQuantidade);
								itemContaHospitalar.setIndOrigem(DominioIndOrigemItemContaHospitalar.BCC);
								itemContaHospitalar.setLocalCobranca(DominioLocalCobranca.I);
								itemContaHospitalar.setDthrRealizado(cirurgia.getDataInicioCirurgia());
								itemContaHospitalar.setUnidadesFuncional(cirurgia.getUnidadeFuncional());
								itemContaHospitalar.setProcEspPorCirurgias(procEspPorCirurgia);

								// INSERE novo item de conta hospitalar
								this.getItemContaHospitalarON().inserirItemContaHospitalarSemValidacoesForms(itemContaHospitalar, true, null, dataFimVinculoServidor, null);

							} else {
								valorMensagem.append("CRG SIT=CANC ");
							}

						} else{
							valorMensagem.append("UPD ICH DECODE('" + procEspPorCirurgia.getSituacao() + "' )'; ");
						}

					} else {

						if (CoreUtil.notIn(contaHospitalar.getIndSituacao(), DominioSituacaoConta.A, DominioSituacaoConta.F)) {
							// Conta já Faturada/Encerrada.
							throw new ApplicationBusinessException(AtualizaFaturamentoBlocoCirurgicoRNExceptionCode.FAT_00620);
						}

						if (contaHospitalar.getMotivoSaidaPaciente() != null) {
							// Conta já possui motivo de saída.
							throw new ApplicationBusinessException(AtualizaFaturamentoBlocoCirurgicoRNExceptionCode.FAT_00621);
						}

					}

				} else {
					// Não existe conta hospitalar no período de realização da cirurgia
					throw new ApplicationBusinessException(AtualizaFaturamentoBlocoCirurgicoRNExceptionCode.FAT_00521);
				}
			}

		} else {
			valorMensagem.append("CNV NAO SUS ");
		}

		// Insere LOGS DE INTERFACE do faturamento
		FatLogInterface logInterface = new FatLogInterface();

		logInterface.setSistema("MBC");
		logInterface.setDthrChamada(new Date());
		logInterface.setProcEspPorCirurgia(procEspPorCirurgia);
		logInterface.setMbcQtd(valorQuantidade.intValue());
		logInterface.setMbcSituacao(procEspPorCirurgia.getSituacao());
		logInterface.setMensagem(valorMensagem.toString());
		logInterface.setLinProcedure("FATP_ATU_FAT_MBC");

		this.getFatLogInterfaceDAO().persistir(logInterface);

	}
	
	/**
	 * Parte de FATP_ATU_FAT_MBC para obter o procedimento hospitalar interno
	 * <p>
	 * CRIADO PARA EVITAR VIOLAÇÕES DE PMD (NPATH COMPLEXITY)
	 * <p> 
	 * @param procEspPorCirurgia
	 * @return
	 * @throws BaseException
	 */
	private FatProcedHospInternos getValorProcedimentoHospitalarInterno(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {
		FatProcedHospInternos valorProcedimentoHospitalarInterno = procEspPorCirurgia.getProcedHospInterno();
		if (valorProcedimentoHospitalarInterno == null) {
			// Busca PHI correpondente ao código de procedimento cirúrgico
			List<FatProcedHospInternos> listaFatProcedHospInternos = this.getFatProcedHospInternosDAO().listarFatProcedHospInternosPorProcedimentoCirurgicos(procEspPorCirurgia.getId().getEprPciSeq());
			if(listaFatProcedHospInternos == null || listaFatProcedHospInternos.isEmpty()){
				// Item da Cirurgia sem correspondente em Procedimento Interno
				throw new ApplicationBusinessException(AtualizaFaturamentoBlocoCirurgicoRNExceptionCode.FAT_00518);
			}
			valorProcedimentoHospitalarInterno = listaFatProcedHospInternos.get(0);
		}
		return valorProcedimentoHospitalarInterno;
	}

	/*
	 * Métodos utilitários
	 */

	/**
	 * Simula DECODE(p_situacao,'I','C','A')
	 * 
	 * @param situacao
	 * @return
	 */
	private DominioSituacaoProcedimentoAmbulatorio getSituacaoProcedimentoAmbulatorio(DominioSituacao situacao) {
		return DominioSituacao.I.equals(situacao) ? DominioSituacaoProcedimentoAmbulatorio.CANCELADO : DominioSituacaoProcedimentoAmbulatorio.ABERTO;
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected VFatAssociacaoProcedimentoDAO getVFatAssociacaoProcedimentoDAO() {
		return vFatAssociacaoProcedimentoDAO;
	}

	protected FatConvenioSaudePlanoDAO getFatConvenioSaudePlanoDAO() {
		return fatConvenioSaudePlanoDAO;
	}

	protected FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}

	protected ProcedimentosAmbRealizadosON getProcedimentosAmbRealizadosON() {
		return procedimentosAmbRealizadosON;
	}

	protected ProcedimentosAmbRealizadoRN getProcedimentosAmbRealizadoRN() {
		return procedimentosAmbRealizadoRN;
	}

	protected VerificacaoFaturamentoSusRN getVerificacaoFaturamentoSusRN() {
		return verificacaoFaturamentoSusRN;
	}

	protected FatLogInterfaceDAO getFatLogInterfaceDAO() {
		return fatLogInterfaceDAO;
	}

	protected FatkCthRN getFatkCthRN() {
		return fatkCthRN;
	}

	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}

	protected FatItemContaHospitalarDAO getFatItemContaHospitalar() {
		return fatItemContaHospitalarDAO;
	}

	
	protected FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}

	protected ItemContaHospitalarON getItemContaHospitalarON() {
		return itemContaHospitalarON;
	}

}
