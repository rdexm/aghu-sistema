package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioModoLancamentoFat;
import br.gov.mec.aghu.dominio.DominioModuloMensagem;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.faturamento.dao.FatAtendimentoApacProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvGrupoItensProcedDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatMensagemLogDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.vo.DadosCaracteristicaTratamentoApacVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatAtendimentoApacProcHosp;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatItemContaApac;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável por manter as regras de negócio da entidade FatProcedAmbRealizado.
 *
 */
@Stateless
public class FatProcedAmbRealizadoRN extends BaseBusiness {

	private static final String FATP_ATU_APAC_CON = "FATP_ATU_APAC_CON";

	private static final long serialVersionUID = -7558634807491425342L;

	private static final Log LOG = LogFactory.getLog(FatProcedAmbRealizadoRN.class);
	
	@EJB
	private ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON;
	
	@EJB
	private TipoCaracteristicaItemRN tipoCaracteristicaItemRN;
	
	@EJB
	private CaracteristicaTratamentoApacRN caracteristicaTratamentoApacRN;
	
	@EJB
	private FatLogErrorON fatLogErrorON;
	
	@EJB
	private FatItemContaApacRN fatItemContaApacRN;
	
	@Inject
	private FatItemContaApacDAO fatItemContaApacDAO;
	
	@Inject
	private FatAtendimentoApacProcHospDAO fatAtendimentoApacProcHospDAO;
	
	@Inject
	private FatConvGrupoItensProcedDAO fatConvGrupoItensProcedDAO;
	
	@Inject
	private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;
	
	@Inject
	private FatMensagemLogDAO fatMensagemLogDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	/**
	 * Realiza a atualização dos procedimentos relacionados a uma consulta, retornando uma flag indicando o sucesso da operação.
	 * 
	 * @param seqAtendimentoAntigo - Código do atendimento antigo
	 * @param seqAtendimentoNovo - Código do novo atendimento
	 * @param numeroConsulta - Número da consulta
	 * @param nomeMicrocomputador - Nome do micro que está realizando a operação
	 * @throws BaseException 
	 */
	public void atualizarProcedimentosConsulta(Integer seqAtendimentoAntigo, Integer seqAtendimentoNovo, Integer numeroConsulta,
			String nomeMicrocomputador) throws BaseException {
		
		// Consulta C17 da estória #37942
		List<FatProcedAmbRealizado> procedimentos = getFatProcedAmbRealizadoDAO()
				.pesquisarProcedimentosRealizadosPorConsultaLiberadaPorObito(numeroConsulta);

		for (FatProcedAmbRealizado fatProcedAmbRealizado : procedimentos) {
			if (seqAtendimentoNovo != null) {
				// Consulta C19 da estória #37942
				List<FatAtendimentoApacProcHosp> atendimentos = getFatAtendimentoApacProcHospDAO()
						.pesquisarAtendimentoPrincipalPorDataCodigo(seqAtendimentoNovo, fatProcedAmbRealizado.getDthrRealizado());

				for (FatAtendimentoApacProcHosp fatAtendimentoApacProcHosp : atendimentos) {
					// Consulta C20 da estória #37942
					List<FatConvGrupoItemProced> itensProcedimento = getFatConvGrupoItensProcedDAO()
							.pesquisarItensProcedimentoAtivosPorCodigoProcedimento(fatAtendimentoApacProcHosp.getId().getPhiSeq());
					
					Integer vTctSeq = getTipoCaracteristicaItemRN().obterTipoCaractItemSeq(DominioFatTipoCaractItem.MODO_LANCAMENTO_FAT);
					
					for (FatConvGrupoItemProced fatConvGrupoItemProced : itensProcedimento) {
						DadosCaracteristicaTratamentoApacVO dadosCaracteristica = getCaracteristicaTratamentoApacRN()
								.verificarCaracteristicaTratamento(fatConvGrupoItemProced.getId().getIphPhoSeq(),
										fatConvGrupoItemProced.getId().getIphSeq(), fatConvGrupoItemProced.getId().getIphPhoSeq(),
										fatConvGrupoItemProced.getId().getIphSeq(), vTctSeq);
						
						String vModoLancto = DominioModoLancamentoFat.O.toString();
						if (dadosCaracteristica != null) {
							vModoLancto = dadosCaracteristica.getValorChar();
						}
						
						if (DominioModoLancamentoFat.C.toString().equals(vModoLancto)
								&& (DominioSituacaoProcedimentoAmbulatorio.ABERTO.equals(fatProcedAmbRealizado.getSituacao())
										|| DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO.equals(fatProcedAmbRealizado.getSituacao()))) {
							try {
								// Consulta C21 da estória #37942
								List<FatProcedAmbRealizado> proceds = getFatProcedAmbRealizadoDAO()
										.buscarPorNumeroConsultaEProcedHospInternos(numeroConsulta, fatAtendimentoApacProcHosp.getId().getPhiSeq());

								for (FatProcedAmbRealizado fatProcedAmbRealizado2 : proceds) {
									fatProcedAmbRealizado2.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
									
									// Parâmetros informados conforme especificado no item de Redmine #42249
									getProcedimentosAmbRealizadosON().atualizarProcedimentoAmbulatorialRealizado(
											fatProcedAmbRealizado2, getFatProcedAmbRealizadoDAO().obterOriginal(fatProcedAmbRealizado2), nomeMicrocomputador,
											new Date(), null);
								}
							} catch (BaseException e) {
								// Consulta C22 da estória #37942
								FatLogError logErro = new FatLogError();
								
								logErro.setModulo(DominioModuloMensagem.APAC.toString());
								logErro.setErro("NAO CANCELOU CONSULTA");
								logErro.setPrograma(FATP_ATU_APAC_CON);
								logErro.setCriadoEm(new Date());
								logErro.setCriadoPor(getServidorLogadoFacade().obterServidorLogado().getUsuario());
								
								getFatLogErrorON().persistirLogError(logErro);
								
								throw e;
							}
							
							// Consulta C18 da estória #37942
							List<FatProcedAmbRealizado> proceds = getFatProcedAmbRealizadoDAO()
									.buscarPorNumeroConsultaEProcedHospInternos(numeroConsulta, fatAtendimentoApacProcHosp.getId().getPhiSeq());
							
							if (proceds.isEmpty()) {
								// Consulta C23 da estória #37942
								try {
									FatProcedAmbRealizado novoProcedimento = new FatProcedAmbRealizado();
									
									novoProcedimento.setDthrRealizado(fatProcedAmbRealizado.getDthrRealizado());
									novoProcedimento.setPaciente(fatProcedAmbRealizado.getPaciente());
									novoProcedimento.setConvenioSaudePlano(fatProcedAmbRealizado.getConvenioSaudePlano());
									novoProcedimento.setUnidadeFuncional(fatProcedAmbRealizado.getUnidadeFuncional());
									novoProcedimento.setEspecialidade(fatProcedAmbRealizado.getEspecialidade());
									novoProcedimento.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
									novoProcedimento.setLocalCobranca(DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B);
									novoProcedimento.setFatCompetencia(fatProcedAmbRealizado.getFatCompetencia());
									novoProcedimento.setProcedimentoHospitalarInterno(fatAtendimentoApacProcHosp.getProcedimentoHospitalarInterno());
									novoProcedimento.setIndOrigem(DominioOrigemProcedimentoAmbulatorialRealizado.CON);
									novoProcedimento.setServidor(new RapServidores(new RapServidoresId(9999999, (short) 955)));
									novoProcedimento.setQuantidade((short) 1);
									if(seqAtendimentoAntigo != null){
										novoProcedimento.setAtendimento(new AghAtendimentos(seqAtendimentoAntigo));
									}
									
									// Parâmetros informados conforme especificado no item de Redmine #42249
									getProcedimentosAmbRealizadosON().inserirFatProcedAmbRealizado(novoProcedimento, nomeMicrocomputador, new Date());
								} catch (BaseException e) {
									// Consulta C24 da estória #37942
									FatLogError logErro = new FatLogError();
									
									logErro.setModulo(DominioModuloMensagem.APAC.toString());
									logErro.setErro("NAO ATUALIZOU SESSOES DE APAC (INS)");
									logErro.setPrograma(FATP_ATU_APAC_CON);
									logErro.setCriadoEm(new Date());
									logErro.setCriadoPor(getServidorLogadoFacade().obterServidorLogado().getUsuario());
									logErro.setCapAtmNumero(fatAtendimentoApacProcHosp.getId().getAtmNumero());
									logErro.setPacCodigo(fatProcedAmbRealizado.getPaciente().getCodigo());
									logErro.setFatMensagemLog(new FatMensagemLog(110));
									
									if(logErro.getFatMensagemLog().getCodigo() != null){
										logErro.setFatMensagemLog(fatMensagemLogDAO.obterFatMensagemLogPorCodigo(logErro.getFatMensagemLog().getCodigo()));
									}
									
									getFatLogErrorON().persistirLogError(logErro);
									
									throw e;
								}
							}
						}
					}
				}
			} else {
				// Consulta C19 da estória #37942
				List<FatAtendimentoApacProcHosp> atendimentos = new ArrayList<FatAtendimentoApacProcHosp>();
				if(seqAtendimentoAntigo != null){
					atendimentos = getFatAtendimentoApacProcHospDAO().pesquisarAtendimentoPrincipalPorDataCodigo(seqAtendimentoAntigo, fatProcedAmbRealizado.getDthrRealizado());
				}

				for (FatAtendimentoApacProcHosp fatAtendimentoApacProcHosp : atendimentos) {
					// Consulta C20 da estória #37942
					List<FatConvGrupoItemProced> itensProcedimento = getFatConvGrupoItensProcedDAO()
							.pesquisarItensProcedimentoAtivosPorCodigoProcedimento(fatAtendimentoApacProcHosp.getId().getPhiSeq());

					Integer vTctSeq = getTipoCaracteristicaItemRN().obterTipoCaractItemSeq(DominioFatTipoCaractItem.MODO_LANCAMENTO_FAT);
					
					for (FatConvGrupoItemProced fatConvGrupoItemProced : itensProcedimento) {
						DadosCaracteristicaTratamentoApacVO dadosCaracteristica = getCaracteristicaTratamentoApacRN()
								.verificarCaracteristicaTratamento(fatConvGrupoItemProced.getId().getIphPhoSeq(),
										fatConvGrupoItemProced.getId().getIphSeq(), fatConvGrupoItemProced.getId().getIphPhoSeq(),
										fatConvGrupoItemProced.getId().getIphSeq(), vTctSeq);
						
						String vModoLancto = DominioModoLancamentoFat.O.toString();
						if (dadosCaracteristica != null) {
							vModoLancto = dadosCaracteristica.getValorChar();
						}

						if (DominioModoLancamentoFat.C.toString().equals(vModoLancto)) {
							if ((DominioSituacaoProcedimentoAmbulatorio.CANCELADO.equals(fatProcedAmbRealizado.getSituacao())
										|| DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO.equals(fatProcedAmbRealizado.getSituacao()))) {
								try {
									// Consulta C21 da estória #37942
									List<FatProcedAmbRealizado> proceds = getFatProcedAmbRealizadoDAO()
											.buscarPorNumeroConsultaEProcedHospInternos(numeroConsulta, fatAtendimentoApacProcHosp.getId().getPhiSeq());

									for (FatProcedAmbRealizado fatProcedAmbRealizado2 : proceds) {
										fatProcedAmbRealizado2.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
										
										// Parâmetros informados conforme especificado no item de Redmine #42249
										getProcedimentosAmbRealizadosON().atualizarProcedimentoAmbulatorialRealizado(
												fatProcedAmbRealizado2, getFatProcedAmbRealizadoDAO().obterOriginal(fatProcedAmbRealizado2),
												nomeMicrocomputador, new Date(), null);
									}
								} catch (BaseException e) {
									// Consulta C22 da estória #37942
									FatLogError logErro = new FatLogError();
									
									logErro.setModulo(DominioModuloMensagem.APAC.toString());
									logErro.setErro("NAO ATIVOU CONSULTA PARA FATURAMENTO EM BPA");
									logErro.setPrograma(FATP_ATU_APAC_CON);
									logErro.setCriadoEm(new Date());
									logErro.setCriadoPor(getServidorLogadoFacade().obterServidorLogado().getUsuario());
									
									getFatLogErrorON().persistirLogError(logErro);
									
									throw e;
								}
							}
							
							try {
								// Consulta C25 da estória #37942
								List<FatItemContaApac> itensConta =  getFatItemContaApacDAO()
										.pesquisarItensProcedimentoPorAtendimentoProcedimentoInternoDataRealizacao(
												fatAtendimentoApacProcHosp.getId().getAtmNumero(), fatAtendimentoApacProcHosp.getId().getPhiSeq(),
												fatProcedAmbRealizado.getDthrRealizado());
								for (FatItemContaApac fatItemContaApac : itensConta) {
									getFatItemContaApacRN().remover(fatItemContaApac);
								}

								// Consulta C26 da estória #37942
								List<FatProcedAmbRealizado> proceds = getFatProcedAmbRealizadoDAO().pesquisarProcedimentosParaExclusaoPorObito(
										seqAtendimentoAntigo, fatAtendimentoApacProcHosp.getId().getPhiSeq(), fatProcedAmbRealizado.getPaciente().getCodigo(),
										fatProcedAmbRealizado.getFatCompetencia().getId().getModulo(),
										fatProcedAmbRealizado.getFatCompetencia().getId().getAno(), fatProcedAmbRealizado.getFatCompetencia().getId().getMes(),
										fatProcedAmbRealizado.getDthrRealizado(), fatProcedAmbRealizado.getFatCompetencia().getId().getDtHrInicio());
								
								for (FatProcedAmbRealizado fatProcedAmbRealizado2 : proceds) {
									// Parâmetros informados conforme especificado no item de Redmine #42249
									getProcedimentosAmbRealizadosON().excluirProcedimentoAmbulatorialRealizado(fatProcedAmbRealizado2, nomeMicrocomputador,
											new Date(), null);
								}
							} catch (BaseException e) {
								// Consulta C22 da estória #37942
								FatLogError logErro = new FatLogError();
								
								logErro.setModulo(DominioModuloMensagem.APAC.toString());
								logErro.setErro("NAO DELETOU PMR");
								logErro.setPrograma(FATP_ATU_APAC_CON);
								logErro.setCriadoEm(new Date());
								logErro.setCriadoPor(getServidorLogadoFacade().obterServidorLogado().getUsuario());
								
								getFatLogErrorON().persistirLogError(logErro);

								throw e;
							}
						}
					}
				}
			}
		}
	}

	/** GET **/
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	protected FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected FatItemContaApacDAO getFatItemContaApacDAO() {
		return fatItemContaApacDAO;
	}

	protected FatAtendimentoApacProcHospDAO getFatAtendimentoApacProcHospDAO() {
		return fatAtendimentoApacProcHospDAO;
	}

	protected FatConvGrupoItensProcedDAO getFatConvGrupoItensProcedDAO() {
		return fatConvGrupoItensProcedDAO;
	}

	protected TipoCaracteristicaItemRN getTipoCaracteristicaItemRN() {
		return tipoCaracteristicaItemRN;
	}

	protected CaracteristicaTratamentoApacRN getCaracteristicaTratamentoApacRN() {
		return caracteristicaTratamentoApacRN;
	}

	protected ProcedimentosAmbRealizadosON getProcedimentosAmbRealizadosON() {
		return procedimentosAmbRealizadosON;
	}

	protected FatLogErrorON getFatLogErrorON() {
		return fatLogErrorON;
	}

	protected FatItemContaApacRN getFatItemContaApacRN() {
		return fatItemContaApacRN;
	}
	
}