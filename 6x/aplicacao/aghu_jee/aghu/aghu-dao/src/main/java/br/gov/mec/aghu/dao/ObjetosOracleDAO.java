package br.gov.mec.aghu.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.jdbc.Work;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dao.ObjetosOracle;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ObjetosOracleException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;


/**
 * Classe responsável pelo acesso às procedures, funcions e packages existentes
 * no Oracle. Todo tipo de acesso as estes tipo de objetos devem ser feitos por
 * aqui
 * 
 * 
 * @see ObjetosOracle
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.NcssTypeCount","PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse" })
public class ObjetosOracleDAO extends BaseDao<ObjetosOracle>{

	private static final long serialVersionUID = -6102516909297573080L;
	
	private static final Log LOG = LogFactory.getLog(ObjetosOracleDAO.class);	
	
	/**
	 * ORADB: Function MAM_TRG_RN.RN_TRGP_DEL_INF_PAC
	 * limpa as informações do paciente (são informações inseridas pela
     * enfermagem por exemplo para serem passadas para o familiar, visitante)
	 * @param trgSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void limpaInformacoesPaciente(final long trgSeq,
			final String usuarioLogado) throws ApplicationBusinessException {
		
				

		if (!super.isHCPA() || !isOracle()) {
			return;
		}
		
		
		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(final Connection connection)
					throws SQLException {

				CallableStatement statement = null;
				try {
					final StringBuilder call = new StringBuilder(
							"{call ");

					call.append(EsquemasOracleEnum.AGH.toString());
					call.append('.');
					call.append(ObjetosBancoOracleEnum.MAMK_TRG_RN_RN_TRGP_DEL_INF_PAC.toString());
					call.append("(?)}");
					
					statement = connection.prepareCall(call.toString());
										
					CoreUtil.configurarParametroCallableStatement(
							statement, 1, Types.INTEGER, trgSeq);				
					
					statement.execute();

				} finally {
					if (statement != null) {
						statement.close();
					}
				}
			}
		};
				

		super.doWork(work);
	}
	
	/**
	 * ORADB: Function MAM_TRG_RN.RN_TRGP_DEL_PAC_LIST
	 * retira o paciente das minhas listas
	 * @param trgSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void retirarPacienteListas(final long trgSeq,
			final String usuarioLogado) throws ApplicationBusinessException {
		
				

		if (!super.isHCPA() || !isOracle()) {
			return;
		}
		
		
		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(final Connection connection)
					throws SQLException {

				CallableStatement statement = null;
				try {
					final StringBuilder call = new StringBuilder(
							"{call ");

					call.append(EsquemasOracleEnum.AGH.toString());
					call.append('.');
					call.append(ObjetosBancoOracleEnum.MAMK_TRG_RN_RN_TRGP_DEL_PAC_LIST.toString());
					call.append("(?)}");
					
					statement = connection.prepareCall(call.toString());
										
					CoreUtil.configurarParametroCallableStatement(
							statement, 1, Types.INTEGER, trgSeq);				
					
					statement.execute();

				} finally {
					if (statement != null) {
						statement.close();
					}
				}
			}
		};
				

		super.doWork(work);
	}
	
	/**
	 * ORADB: Function MAM_TRG_RN.RN_TRGP_DEL_PLANO
	 * limpa o plano de ação do paciente
	 * @param trgSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void limparPlanoAcaoPaciente(final long trgSeq,
			final String usuarioLogado) throws ApplicationBusinessException {
		
				

		if (!super.isHCPA() || !isOracle()) {
			return;
		}
		
		
		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(final Connection connection)
					throws SQLException {

				CallableStatement statement = null;
				try {
					final StringBuilder call = new StringBuilder(
							"{call ");

					call.append(EsquemasOracleEnum.AGH.toString());
					call.append('.');
					call.append(ObjetosBancoOracleEnum.MAMK_TRG_RN_RN_TRGP_DEL_PLANO.toString());
					call.append("(?)}");
					
					statement = connection.prepareCall(call.toString());
										
					CoreUtil.configurarParametroCallableStatement(
							statement, 1, Types.INTEGER, trgSeq);				
					
					statement.execute();

				} finally {
					if (statement != null) {
						statement.close();
					}
				}
			}
		};
				

		super.doWork(work);
	}
	
	/**
	 * ORADB: Function MAM_TRG_RN.RN_TRGP_DEL_RESUMO
	 * limpa o resumo de caso do paciente
	 * @param trgSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void limparResumoCasoPaciente(final long trgSeq,
			final String usuarioLogado) throws ApplicationBusinessException {
		
				

		if (!super.isHCPA() || !isOracle()) {
			return;
		}
		
		
		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(final Connection connection)
					throws SQLException {

				CallableStatement statement = null;
				try {
					final StringBuilder call = new StringBuilder(
							"{call ");

					call.append(EsquemasOracleEnum.AGH.toString());
					call.append('.');
					call.append(ObjetosBancoOracleEnum.MAMK_TRG_RN_RN_TRGP_DEL_RESUMO.toString());
					call.append("(?)}");
					
					statement = connection.prepareCall(call.toString());
										
					CoreUtil.configurarParametroCallableStatement(
							statement, 1, Types.INTEGER, trgSeq);				
					
					statement.execute();

				} finally {
					if (statement != null) {
						statement.close();
					}
				}
			}
		};
				
		super.doWork(work);
	}
	/**
	 * ORADB: Function MAMK_TRG_RN.RN_TRGP_CHECK_OUT
	 * 
	 * @param segSeqAnt, segSeqAtual, trgSeq, dataUltMovimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void faturamentoCheckOut(final int segSeqAnt,
			final int segSeqAtual, 
			final int trgSeq, 
			final java.util.Date dataUltMovimento,
			final String usuarioLogado) throws ApplicationBusinessException {
		
				

		if (!super.isHCPA() || !isOracle()) {
			return;
		}
		
		
		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(final Connection connection)
					throws SQLException {

				CallableStatement statement = null;
				try {
					final StringBuilder call = new StringBuilder(
							"{call ");

					call.append(EsquemasOracleEnum.AGH.toString());
					call.append('.');
					call.append(ObjetosBancoOracleEnum.MAMK_TRG_RN_RN_TRGP_CHECK_OUT.toString());
					call.append("(?,?,?,?)}");
					
					statement = connection.prepareCall(call.toString());
					
					CoreUtil.configurarParametroCallableStatement(
							statement, 1, Types.INTEGER, segSeqAnt);
					CoreUtil.configurarParametroCallableStatement(
							statement, 2, Types.INTEGER, segSeqAtual);
					CoreUtil.configurarParametroCallableStatement(
							statement, 3, Types.INTEGER, trgSeq);					
					CoreUtil.configurarParametroCallableStatement(
							statement, 4, Types.TIMESTAMP, dataUltMovimento);					

					statement.execute();

				} finally {
					if (statement != null) {
						statement.close();
					}
				}
			}
		};
				

		super.doWork(work);
	}

	/**
	 * ORADB: Procedure MAMP_CHECK_OUT_INT
	 */
	public void gerarCheckOut(final Integer seq, final Integer pacCodigo,
			final String tipoAltaMedicaCodigoOld,
			final String tipoAltaMedicaCodigo, final Short unfSeqOld,
			final Short unfSeq, final Boolean pacienteInternadoOld,
			final Boolean pacienteInternacao, RapServidores servidorLogado)
			throws ObjetosOracleException {

		if (!isOracle()) {
			return;
		}

		final String nomeObjeto = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.MAMP_CHECK_OUT_INT;
		
		AghWork work =  
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {
					@Override
					public void executeAghProcedure(
							final Connection connection)
							throws SQLException {

						CallableStatement cs = null;
						try {
							final StringBuilder sbCall = new StringBuilder(50).append(
									"{call ");
							sbCall.append(nomeObjeto);
							sbCall.append("(?,?,?,?,?,?,?,?)}");

							cs = connection.prepareCall(sbCall.toString());

							CoreUtil.configurarParametroCallableStatement(
									cs, 1, Types.INTEGER, seq);
							CoreUtil.configurarParametroCallableStatement(
									cs, 2, Types.INTEGER, pacCodigo);
							CoreUtil.configurarParametroCallableStatement(
									cs, 3, Types.VARCHAR,
									tipoAltaMedicaCodigoOld);
							CoreUtil.configurarParametroCallableStatement(
									cs, 4, Types.VARCHAR,
									tipoAltaMedicaCodigo);
							CoreUtil.configurarParametroCallableStatement(
									cs, 5, Types.INTEGER, unfSeqOld);
							CoreUtil.configurarParametroCallableStatement(
									cs, 6, Types.INTEGER, unfSeq);

							// CoreUtil não trata Types.BOOLEAN
							if (pacienteInternadoOld == null) {
								cs.setNull(7, Types.INTEGER);
							} else {
								cs.setBoolean(7, pacienteInternadoOld);
							}

							if (pacienteInternacao == null) {
								cs.setNull(8, Types.INTEGER);
							} else {
								cs.setBoolean(8, pacienteInternacao);
							}

							cs.execute();
						} finally {
							if (cs != null) {
								cs.close();
							}
						}
					}
				};
		try {
			doWork(work);
		} catch (final Exception e) {
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(seq,
							pacCodigo, tipoAltaMedicaCodigoOld,
							tipoAltaMedicaCodigo, unfSeqOld, unfSeq,
							pacienteInternadoOld, pacienteInternacao);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							e, true, valores), e);
			throw new ObjetosOracleException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, e, false, valores));
		}
		if (work.getException() != null){
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(seq,
							pacCodigo, tipoAltaMedicaCodigoOld,
							tipoAltaMedicaCodigo, unfSeqOld, unfSeq,
							pacienteInternadoOld, pacienteInternacao);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							work.getException(), true, valores), work.getException());
			throw new ObjetosOracleException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, work.getException(), false, valores));
		}
	}

	@SuppressWarnings("deprecation")
	public void encerrarRonda(final Integer aciIntSeq,
			final java.util.Date dthrFim, RapServidores servidorLogado)
			throws ObjetosOracleException {
		if (!isOracle()) {
			return;
		}
		final String nomeObjeto = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.AINP_ENCERRA_RONDA;
		
		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {
					@Override
					public void executeAghProcedure(
							final Connection connection)
							throws SQLException {

						CallableStatement cs = null;
						try {
							final StringBuilder sbCall = new StringBuilder(
									"{call ").append(nomeObjeto).append(
									"(?,?)}");

							cs = connection.prepareCall(sbCall.toString());

							CoreUtil.configurarParametroCallableStatement(
									cs, 1, Types.INTEGER, aciIntSeq);
							CoreUtil.configurarParametroCallableStatement(
									cs, 2, Types.TIMESTAMP, dthrFim);

							cs.execute();
						} finally {
							if (cs != null) {
								cs.close();
							}
						}
					}
				};

		try {
			super.doWork(work);
		} catch (final Exception e) {
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(aciIntSeq,
							dthrFim);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							e, true, valores), e);
			throw new ObjetosOracleException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, e, false, valores));
		}
		if (work.getException() != null){
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(aciIntSeq,
							dthrFim);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							work.getException(), true, valores), work.getException());
			throw new ObjetosOracleException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, work.getException(), false, valores));
		}
	}

	/**
	 * ORADB: Procedure MAMP_ATU_TRG_EMATEND
	 */
	public void atualizarSituacaoTriagem(final Integer pacCodigo,
			RapServidores servidorLogado) throws ObjetosOracleException {

		if (!isOracle()) {
			return;
		}
		final String nomeObjeto = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.MAMP_ATU_TRG_EMATEND;
		
		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {
					@Override
					public void executeAghProcedure(
							final Connection connection)
							throws SQLException {

						CallableStatement cs = null;
						try {
							final StringBuilder sbCall = new StringBuilder(
									"{call ");
							sbCall.append(nomeObjeto);
							sbCall.append("(?)}");

							cs = connection.prepareCall(sbCall.toString());

							CoreUtil.configurarParametroCallableStatement(
									cs, 1, Types.INTEGER, pacCodigo);

							cs.execute();
						} finally {
							if (cs != null) {
								cs.close();
							}
						}
					}
				};
		
		try {
			super.doWork(work);
		} catch (final Exception e) {
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(pacCodigo);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							e, true, valores), e);
			throw new ObjetosOracleException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, e, false, valores));
		}
		

		if (work.getException() != null) {
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(pacCodigo);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							work.getException(), true, valores), work.getException());
			throw new ObjetosOracleException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, work.getException(), false,
									valores));
		}
	}

	public void cancelarExameProvaCruzadaTransfusional(final Integer atdSeq, final Integer crgSeq, final RapServidores servidorLogado) throws ApplicationBusinessException {
		if (isOracle()) {
			final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AELK_EXAME_PCT_AELP_CANCELA_CIRURGIA.toString();
			
			AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {
				
				@Override
				public void executeAghProcedure(Connection connection) throws SQLException {
					CallableStatement statement = null;					
					try {
						final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?)}");

						statement = connection.prepareCall(call.toString());

						statement.setInt(1, atdSeq); //P_ATD_SEQ IN NUMBER
						statement.setInt(2, crgSeq); //P_CRG_SEQ IN NUMBER
						
						statement.execute();
					} finally {
						if (statement != null) {
							statement.close();
						}
					}
				}
			};
			
			try {
				super.doWork(work);
				LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.AELK_EXAME_PCT_AELP_CANCELA_CIRURGIA.toString());
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, atdSeq, crgSeq);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq, crgSeq);
			}
		}
	}

	public void executarFaturamentoTrocaConvenioPlano(final Integer atdSeq,
			final Integer crgSeq, final Short convCodigo,
			final Byte convSeq, final Short convCodigoOld,
			final Byte convSeqOld, final String usuarioLogado) throws ApplicationBusinessException {
		if (isOracle()) {
			final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.FATK_SUS_RN_RN_FATP_ATU_TRCNVMBC.toString();
			
			AghWork work = new AghWork(usuarioLogado) {
				
				@Override
				public void executeAghProcedure(Connection connection) throws SQLException {

					CallableStatement statement = null;					
					try {
						final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?,?,?)}");

						statement = connection.prepareCall(call.toString());

						statement.setInt(1, atdSeq);
						statement.setInt(2, crgSeq);
						statement.setShort(3, convCodigo);
						statement.setByte(4, convSeq);
						statement.setShort(5, convCodigoOld);
						statement.setByte(6, convSeqOld);
						
						statement.execute();
					} finally {
						if (statement != null) {
							statement.close();
						}
					}
				}
			};
			
			try {
				super.doWork(work);
				LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.FATK_SUS_RN_RN_FATP_ATU_TRCNVMBC.toString());
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, atdSeq, crgSeq, convCodigo, convSeq, convCodigoOld, convSeqOld);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq, crgSeq, convCodigo, convSeq, convCodigoOld, convSeqOld);
			}
		}
	}
	
	public void gerarExameProvaCruzadaTransfusional(final Integer atdSeq, final Integer crgSeq,
			final RapServidores servidorLogado,
			final RapServidores responsavel, final String validaHemocomponente)
			throws ApplicationBusinessException {

		if (isOracle()) {
			final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AELK_EXAME_PCT_AELP_GERA_EXAME_PCT.toString();
			
			AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {
				
				@Override
				public void executeAghProcedure(Connection connection) throws SQLException {
					CallableStatement statement = null;
					
					try {
						final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?,?,?)}");

						statement = connection.prepareCall(call.toString());
						
						CoreUtil.configurarParametroCallableStatement(statement, 1, Types.INTEGER, atdSeq); //P_ATD_SEQ IN NUMBER
						CoreUtil.configurarParametroCallableStatement(statement, 2, Types.INTEGER, crgSeq); //P_CRG_SEQ IN NUMBER
						CoreUtil.configurarParametroCallableStatement(statement, 3, Types.INTEGER, responsavel.getId().getMatricula()); //P_SER_MATRICULA IN NUMBER
						CoreUtil.configurarParametroCallableStatement(statement, 4, Types.INTEGER, responsavel.getId().getVinCodigo()); //P_SER_VIN_CODIGO IN NUMBER
						CoreUtil.configurarParametroCallableStatement(statement, 5, Types.VARCHAR, validaHemocomponente); //P_TESTA_HEMOCOMP IN VARCHAR2

						statement.registerOutParameter(6, Types.VARCHAR);
						
						statement.execute();
					} finally {
						if (statement != null) {
							statement.close();
						}
					}
				}
			};
			
			try {
				super.doWork(work);
				LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.AELK_EXAME_PCT_AELP_GERA_EXAME_PCT.toString());
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, atdSeq,
						crgSeq, responsavel.getId().getMatricula(), responsavel
								.getId().getVinCodigo(), validaHemocomponente);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq,
						crgSeq, responsavel.getId().getMatricula(), responsavel
						.getId().getVinCodigo(), validaHemocomponente);
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_PPR_RN.RN_PPRP_DEL_UOP_PROT
	 * 
	 */
	public void mpmkPprRnRnPprpDelUopProt(final Integer atdSeq, final Long seq,
			RapServidores servidorLogado) throws ObjetosOracleException {
		if (!isOracle()) {
			return;
		}

		final String nomeObjeto = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.RN_PPRP_DEL_UOP_PROT.toString();
		
		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {
					@Override
					public void executeAghProcedure(
							final Connection connection)
							throws SQLException {

						CallableStatement cs = null;
						try {
							final StringBuilder sbCall = new StringBuilder(
									"{call ");
							sbCall.append(nomeObjeto);
							sbCall.append("(?,?)}");

							cs = connection.prepareCall(sbCall.toString());

							CoreUtil.configurarParametroCallableStatement(
									cs, 1, Types.INTEGER, atdSeq);
							CoreUtil.configurarParametroCallableStatement(
									cs, 2, Types.INTEGER, seq.intValue());

							cs.execute();
						} finally {
							if (cs != null) {
								cs.close();
							}
						}
					}
				};
		
		try {
			super.doWork(work);
		} catch (final Exception e) {
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(atdSeq, seq);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							e, true, valores), e);
			throw new ObjetosOracleException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, e, false, valores));
		}
		if (work.getException() != null){
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(atdSeq, seq);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							work.getException(), true, valores), work.getException());
			throw new ObjetosOracleException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, work.getException(), false, valores));
		}
	}

	/**
	 * Método para chamar procedure do módulo de faturamento.
	 * 
	 * ORADB Procedure FATK_CTH4_RN_UN.RN_CTHP_BUSCA_SANGUE
	 * 
	 * @param seqContaInternacao
	 */
	public void buscarSangue(final Integer seqContaInternacao,
			RapServidores servidorLogado) throws ObjetosOracleException {

		if (seqContaInternacao != null) {
			if (!isOracle()) {
				return;
			}
			final String nomeObjeto = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.FATK_CTH4_RN_UN_RN_CTHP_BUSCA_SANGUE;
			
			AghWork work = new AghWork(
					servidorLogado != null ? servidorLogado
							.getUsuario() : null) {
				@Override
				public void executeAghProcedure(
						final Connection connection)
						throws SQLException {

					CallableStatement cs = null;
					try {
						cs = connection.prepareCall("{call "
								+ nomeObjeto + "(?)}");
						CoreUtil.configurarParametroCallableStatement(
								cs, 1, Types.INTEGER,
								seqContaInternacao);
						cs.execute();
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			};

			try {
				super.doWork(work);
			} catch (final Exception e) {
				final String valores = CoreUtil
						.configurarValoresParametrosCallableStatement(seqContaInternacao);
				LOG.error(CoreUtil
						.configurarMensagemUsuarioLogCallableStatement(
								nomeObjeto, e, true, valores), e);
				throw new ObjetosOracleException(
						AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
						nomeObjeto, valores, CoreUtil
								.configurarMensagemUsuarioLogCallableStatement(
										nomeObjeto, e, false, valores));
			}
			
			if (work.getException() != null){
				final String valores = CoreUtil
						.configurarValoresParametrosCallableStatement(seqContaInternacao);
				LOG.error(CoreUtil
						.configurarMensagemUsuarioLogCallableStatement(
								nomeObjeto, work.getException(), true, valores), work.getException());
				throw new ObjetosOracleException(
						AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
						nomeObjeto, valores, CoreUtil
								.configurarMensagemUsuarioLogCallableStatement(
										nomeObjeto, work.getException(), false, valores));
			}
		}
	}

	/**
	 * Verifica se servidor tem registro ativo no sistema STARH.<br>
	 * ORADB: Function RAPC_VER_SERV_ATI_STARH
	 * 
	 * @param pessoaFisica
	 * @param servidor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean servidorAtivoStarh(final RapPessoasFisicas pessoaFisica,
			final RapServidores servidor, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		if (pessoaFisica == null && servidor == null) {
			throw new IllegalArgumentException(
					"Um dos argumentos é obrigatório.");
		}

		// TODO: já esta verificando isOracle(), pendente de
		// instruções para o uso devido
		// se não é oracle não tem sistema starh
		// if (!isCompativelOracle()) {
		// return false;
		// }

		// TODO: tratar possivel exceção vinda do banco de dados conforme
		// http://redmine.mec.gov.br/projects/aghu/wiki/Chamadas_de_procedures_de_banco
		//
		final List<Boolean> result = new ArrayList<Boolean>();
		//
		final String storedProcedureName = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.RAPC_VER_SERV_ATI_STARH;
		
		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {

					@Override
					public void executeAghProcedure(
							final Connection connection)
							throws SQLException {
						CallableStatement statement = null;

						try {
							final StringBuilder call = new StringBuilder(
									"{? = call ");
							call.append(storedProcedureName);
							call.append("(?,?)}");

							statement = connection.prepareCall(call
									.toString());

							statement.registerOutParameter(1,
									java.sql.Types.VARCHAR);
							if (pessoaFisica != null) {
								statement.setInt(2,
										pessoaFisica.getCodigo());
								statement
										.setNull(3, java.sql.Types.NUMERIC);
							} else {
								statement.setInt(3, servidor.getId()
										.getMatricula());
								statement
										.setNull(2, java.sql.Types.NUMERIC);
							}

							statement.execute();

							final String retorno = statement.getString(1);
							//
							result.add(retorno.equals("S"));

						} finally {
							if (statement != null) {
								statement.close();
							}
						}

					}
				};
		//
		try {
			super.doWork(work);
		} catch (final Exception e) {
			throwExceptionStoredProcedure(storedProcedureName, e, pessoaFisica,
					servidor);
		}
		
		if (work.getException() != null){
			throwExceptionStoredProcedure(storedProcedureName, work.getException(), pessoaFisica,
					servidor);
		}

		return result.get(0);
	}

	/**
	 * Revogar perfis do servidor quando houver alguma alteração
	 * 
	 * @param matricula
	 * @param codigoVinculo
	 * @param alteraCCusto
	 * @throws ApplicationBusinessException
	 */
	public void revogarPerfisServidor(final Integer matricula,
			final Short codigoVinculo, final String alteraCCusto,
			final String perfil, RapServidores servidorLogado)
			throws ApplicationBusinessException {

		if (matricula == null || codigoVinculo == null) {
			throw new IllegalArgumentException("Argumentos obrigatórios.");
		}

		if (!isOracle()) {
			return;
		}

		final String storedProcedureName = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.CSEP_REVOGA_PERFIS.toString();

		
		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {

					@Override
					public void executeAghProcedure(
							final Connection connection)
							throws SQLException {
						CallableStatement statement = null;

						try {
							final StringBuilder call = new StringBuilder(
									"{call ");
							call.append(storedProcedureName);
							call.append("(?,?,?,?)}");

							statement = connection.prepareCall(call
									.toString());

							statement.setInt(1, matricula);
							statement.setShort(2, codigoVinculo);

							if (alteraCCusto == null) {
								statement
										.setNull(3, java.sql.Types.VARCHAR);
							} else {
								statement.setString(3, alteraCCusto);
							}
							if (perfil == null) {
								statement
										.setNull(4, java.sql.Types.VARCHAR);
							} else {
								statement.setString(4, perfil);
							}

							statement.execute();

						} finally {
							if (statement != null) {
								statement.close();
							}
						}

					}
				};
		
		try {
			super.doWork(work);
		} catch (final Exception e) {
			throwExceptionStoredProcedure(storedProcedureName, e, matricula,
					codigoVinculo, alteraCCusto);
		}
		if (work.getException() != null){
			throwExceptionStoredProcedure(storedProcedureName, work.getException(), matricula,
					codigoVinculo, alteraCCusto);
		}
	}

	/**
	 * Concede perfis ao servidor quando houver alguma alteração
	 * 
	 * @param matricula
	 * @param codigoVinculo
	 * @param alteraCCusto
	 * @throws ApplicationBusinessException
	 */
	public void fornecerPerfisServidor(final Integer matricula,
			final Short codigoVinculo, final String alteraCCusto,
			final String perfil, RapServidores servidorLogado)
			throws ApplicationBusinessException {

		if (matricula == null || codigoVinculo == null) {
			throw new IllegalArgumentException("Argumentos obrigatórios.");
		}

		if (!isOracle()) {
			return;
		}

		final String storedProcedureName = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.CSEP_FORNECE_PERFIS.toString();
		
		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {

					@Override
					public void executeAghProcedure(
							final Connection connection)
							throws SQLException {

						CallableStatement statement = null;
						try {
							final StringBuilder call = new StringBuilder(
									"{call ");
							call.append(storedProcedureName);
							call.append("(?,?,?,?)}");

							statement = connection.prepareCall(call
									.toString());

							statement.setInt(1, matricula);
							statement.setShort(2, codigoVinculo);
							if (alteraCCusto == null) {
								statement
										.setNull(3, java.sql.Types.VARCHAR);
							} else {
								statement.setString(3, alteraCCusto);
							}
							if (perfil == null) {
								statement
										.setNull(4, java.sql.Types.VARCHAR);
							} else {
								statement.setString(4, perfil);
							}

							statement.execute();

						} finally {
							if (statement != null) {
								statement.close();
							}
						}

					}
				};
		try {
			super.doWork(work);
		} catch (final Exception e) {
			throwExceptionStoredProcedure(storedProcedureName, e, matricula,
					codigoVinculo, alteraCCusto);
		}
		
		if (work.getException() != null){
			throwExceptionStoredProcedure(storedProcedureName, work.getException(), matricula,
					codigoVinculo, alteraCCusto);
		}
	}

	public void ajustarEmailNT(final Integer matricula,
			final Short codigoVinculo, RapServidores servidorLogado)
			throws ApplicationBusinessException {

		if (matricula == null || codigoVinculo == null) {
			throw new IllegalArgumentException("Argumentos obrigatórios.");
		}

		if (!this.isProducaoHCPA()) {
			return;
		}

		if (!isOracle()) {
			return;
		}

		final String storedProcedureName = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.CSEP_AJUSTA_NT_EMAIL;
		
		AghWork work = new AghWork(servidorLogado != null ? servidorLogado
				.getUsuario() : null) {

			@Override
			public void executeAghProcedure(
					final Connection connection)
					throws SQLException {

				CallableStatement statement = null;
				try {
					final StringBuilder call = new StringBuilder(
							"{call ");
					call.append(storedProcedureName);
					call.append("(?,?,?)}");

					statement = connection.prepareCall(call
							.toString());

					statement.setInt(1, matricula);
					statement.setShort(2, codigoVinculo);
					statement.setString(3, "N");

					statement.execute();

				} finally {
					if (statement != null) {
						statement.close();
					}
				}

			}
		};
		try {			
			super.doWork(work);
		} catch (final Exception e) {
			throwExceptionStoredProcedure(storedProcedureName, e, matricula,
					codigoVinculo);
		}
		
		if (work.getException() != null){
			throwExceptionStoredProcedure(storedProcedureName, work.getException(), matricula,
					codigoVinculo);
		}
		
	}

	/**
	 * ORADB: Procedure RN_SERP_AVISO_ENCERRAMENTO
	 * 
	 * @param matricula
	 * @param codigoVinculo
	 * @throws ApplicationBusinessException
	 */
	public void avisarEncerramentoVinculo(final Integer matricula,
			final Short codigoVinculo, RapServidores servidorLogado)
			throws ApplicationBusinessException {

		if (matricula == null || codigoVinculo == null) {
			throw new IllegalArgumentException("Argumentos obrigatórios.");
		}

		if (!isOracle()) {
			return;
		}

		final String storedProcedureName = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.RN_SERP_AVISO_ENCERRAMENTO.toString();

		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {

					@Override
					public void executeAghProcedure(
							final Connection connection)
							throws SQLException {

						CallableStatement statement = null;
						try {
							final StringBuilder call = new StringBuilder(
									"{call ");
							call.append(storedProcedureName);
							call.append("(?,?)}");

							statement = connection.prepareCall(call
									.toString());

							statement.setShort(1, codigoVinculo);
							statement.setInt(2, matricula);

							statement.execute();

						} finally {
							if (statement != null) {
								statement.close();
							}
						}

					}
				};
		try {
			super.doWork(work);
		} catch (final Exception e) {
			throwExceptionStoredProcedure(storedProcedureName, e, matricula,
					codigoVinculo);
		}
		if (work.getException() != null){
			throwExceptionStoredProcedure(storedProcedureName, work.getException(), matricula,
					codigoVinculo);
		}

	}

	/**
	 * ORADB: Procedure RAPK_SER_RN_AGHU.RN_SERP_VERIFICA_NT_EMAIL
	 * 
	 * @param matricula
	 * @param codigoVinculo
	 * @throws ApplicationBusinessException
	 */
	public void ativarContaNtEmail(final Integer matricula,
			final Short codigoVinculo, RapServidores servidorLogado)
			throws ApplicationBusinessException {

		if (matricula == null || codigoVinculo == null) {
			throw new IllegalArgumentException("Argumentos obrigatórios.");
		}

		if (!isOracle()) {
			return;
		}

		final String storedProcedureName = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.RN_SERP_VERIFICA_NT_EMAIL.toString();
		
		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {

					@Override
					public void executeAghProcedure(
							final Connection connection)
							throws SQLException {

						CallableStatement statement = null;
						try {
							final StringBuilder call = new StringBuilder(
									"{call ");
							call.append(storedProcedureName);
							call.append("(?,?)}");

							statement = connection.prepareCall(call
									.toString());

							statement.setInt(1, matricula);
							statement.setShort(2, codigoVinculo);

							statement.execute();

						} finally {
							if (statement != null) {
								statement.close();
							}
						}

					}
				};
		try {
			super.doWork(work);
		} catch (final Exception e) {
			throwExceptionStoredProcedure(storedProcedureName, e, matricula,
					codigoVinculo);
		}
		if (work.getException() != null){
			throwExceptionStoredProcedure(storedProcedureName, work.getException(), matricula,
					codigoVinculo);
		}

	}

	/**
	 * ORADB: Procedure RN_SERP_ATUALIZA_LIDERES
	 * 
	 * @param matricula
	 * @param codigoVinculo
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void atualizarLideres(final Integer codigoCCustoDesempenho,
			final Integer codigoCCustoDesempenhoVelho,
			final Integer codigoCCustoLotacao,
			final Integer codigoCCustoLotacaoVelho, final Short codigoVinculo,
			final Short codigoVinculoVelho, final Integer matricula,
			final Integer matriculaVelho, final String nomePessoaFisica,
			RapServidores servidorLogado) throws ApplicationBusinessException {

		if (matricula == null || codigoVinculo == null
				|| codigoVinculoVelho == null || matriculaVelho == null) {
			throw new IllegalArgumentException("Argumentos obrigatórios.");
		}

		if (!isOracle()) {
			return;
		}

		final String storedProcedureName = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.RN_SERP_ATUALIZA_LIDERES.toString();
		
		String usuarioLogado = null;
		if (servidorLogado != null) {
			usuarioLogado = servidorLogado.getUsuario();
		}
		
		AghWork work = new AghWork(usuarioLogado) {

			@Override
			public void executeAghProcedure(final Connection connection)
					throws SQLException {

				CallableStatement statement = null;
				try {
					final StringBuilder call = new StringBuilder(50).append("{call ");
					call.append(storedProcedureName);
					call.append("(?,?,?,?,?,?,?,?,?)}");

					statement = connection.prepareCall(call.toString());

					if (codigoCCustoDesempenho != null) {
						statement.setInt(1, codigoCCustoDesempenho);
					} else {
						statement.setNull(1, java.sql.Types.INTEGER);
					}

					if (codigoCCustoDesempenhoVelho != null) {
						statement.setInt(2, codigoCCustoDesempenhoVelho);
					} else {
						statement.setNull(2, java.sql.Types.INTEGER);
					}

					if (codigoCCustoLotacao != null) {
						statement.setInt(3, codigoCCustoLotacao);
					} else {
						statement.setNull(3, java.sql.Types.INTEGER);
					}

					if (codigoCCustoLotacaoVelho != null) {
						statement.setInt(4, codigoCCustoLotacaoVelho);
					} else {
						statement.setNull(4, java.sql.Types.INTEGER);
					}

					statement.setShort(5, codigoVinculo);
					statement.setShort(6, codigoVinculoVelho);
					statement.setInt(7, matricula);
					statement.setInt(8, matriculaVelho);

					if (nomePessoaFisica != null) {
						statement.setString(9, nomePessoaFisica);
					} else {
						statement.setNull(9, java.sql.Types.VARCHAR);
					}

					statement.execute();

				} finally {
					if (statement != null) {
						statement.close();
					}
				}

			}
		};
		
		try {
			super.doWork(work);
		} catch (final Exception e) {
			throwExceptionStoredProcedure(storedProcedureName, e, matricula,
					codigoVinculo);
		}
		if (work.getException() != null){
			throwExceptionStoredProcedure(storedProcedureName, work.getException() , matricula,
					codigoVinculo);
		}
	}

	/**
	 * ORADB: Function RAPK_SER_RN_AGHU.RN_SERC_VER_PERFIL_VINC
	 * 
	 * @param codigoVinculo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean perfilVinculo(final Short codigoVinculo,
			final String usuarioLogado) throws ApplicationBusinessException {
		
		Boolean retorno = null;
		
		if (codigoVinculo == null) {
			throw new IllegalArgumentException("Argumento é obrigatório.");
		}

		if (!super.isHCPA() || !isOracle()) {
			return true;
		}

		//
		final List<Boolean> result = new ArrayList<Boolean>();
		
		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(final Connection connection)
					throws SQLException {

				CallableStatement statement = null;
				try {
					final StringBuilder call = new StringBuilder(
							"{? = call ");

					call.append(EsquemasOracleEnum.AGH.toString());
					call.append('.');
					call.append(ObjetosBancoOracleEnum.RN_SERC_VER_PERFIL_VINC
							.toString());
					call.append("(?,?)}");

					statement = connection.prepareCall(call.toString());
					statement.registerOutParameter(1,
							java.sql.Types.VARCHAR);
					statement.setInt(2, codigoVinculo);
					statement.setString(3, usuarioLogado);
					statement.execute();

					final String retorno = statement.getString(1);
					//
					result.add(retorno.equals("S"));
				} finally {
					if (statement != null) {
						statement.close();
					}
				}
			}
		};
		
		try {
			super.doWork(work);
			retorno = result.get(0);
		} catch (final Exception e) {
			throwExceptionStoredProcedure(
					ObjetosBancoOracleEnum.RN_SERC_VER_PERFIL_VINC.toString(),
					e, codigoVinculo, usuarioLogado);
		}
		
		if (work.getException() != null){
			throwExceptionStoredProcedure(
					ObjetosBancoOracleEnum.RN_SERC_VER_PERFIL_VINC.toString(),
					work.getException(), codigoVinculo, usuarioLogado);
		}
		
		return retorno;
	}

	/**
	 * Verifica se um dependente possui convenio Unimed.
	 * 
	 * @return {@code true}, caso possua convenio e {@code false}, para caso
	 *         contrario.
	 */
	public Boolean verificarConvenioUnimed(final Short codigoVinculo,
			final Integer codigoDependente, final Integer matricula,
			final long dataAtual, RapServidores servidorLogado)
			throws ApplicationBusinessException {

		if (codigoVinculo == null || codigoDependente == null
				|| (matricula == null) || (dataAtual <= 0)) {
			throw new IllegalArgumentException("Argumentos são obrigatórios.");
		}

		if (!super.isHCPA() || !isOracle()) {
			return true;
		}
		
		final List<Boolean> result = new ArrayList<Boolean>();
		
		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {
					@Override
					public void executeAghProcedure(final Connection connection)
							throws SQLException {

						CallableStatement statement = null;
						final Date dtAtual = new Date(dataAtual);
						try {
							final StringBuilder call = new StringBuilder(
									"{? = call ");

							call.append(EsquemasOracleEnum.AGH.toString());
							call.append('.');
							call.append(ObjetosBancoOracleEnum.RAPC_VESETEM_UNIMED);
							call.append("(?,?,?,?,?)}");

							statement = connection.prepareCall(call.toString());
							statement.registerOutParameter(1,
									java.sql.Types.VARCHAR);
							statement.setInt(2, matricula);
							statement.setInt(3, codigoVinculo);
							statement.setInt(4, codigoDependente);
							statement.setString(5, null);
							statement.setDate(6, dtAtual);
							statement.execute();

							final String retorno = statement.getString(1);
							//
							// result.add(retorno.equals("S"));
							if (!retorno.equals("N")) {
								result.add(true);
							} else {
								result.add(false);
							}

						} finally {
							if (statement != null) {
								statement.close();
							}
						}
					}
				};

		
		super.doWork(work);
		
		
		if (work.getException() != null){
			throw new HibernateException(work.getException());
		}

		return result.get(0);
	}

	/**
	 * ORADB: Function RAPC_BUSCA_COD_STARH
	 * 
	 * @param codigoVinculo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Integer buscarCodigoMatricula(RapServidores servidorLogado)
			throws ApplicationBusinessException {

		if (!isOracle()) {
			return null;
		}
		//
		final List<Integer> result = new ArrayList<Integer>();
		
		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {
					@Override
					public void executeAghProcedure(final Connection connection)
							throws SQLException {

						CallableStatement statement = null;
						try {
							final StringBuilder call = new StringBuilder(
									"{? = call ");

							call.append(EsquemasOracleEnum.AGH.toString());
							call.append('.');
							call.append(ObjetosBancoOracleEnum.RAPC_BUSCA_COD_STARH);
							call.append("()}");

							statement = connection.prepareCall(call.toString());
							statement.registerOutParameter(1,
									java.sql.Types.INTEGER);
							statement.execute();

							result.add(statement.getInt(1));

						} finally {
							if (statement != null) {
								statement.close();
							}
						}
					}
				};
		
		super.doWork(work);
		
		if (work.getException() != null){
			throw new HibernateException(work.getException());
		}
		return result.get(0);
	}

	/**
	 * ORADB: Function RAPC_FUNCAO_CRACHA
	 * 
	 * @param codigoVinculo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String buscarFuncaoCracha(final Integer matricula,
			final Short codigoVinculo, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		
		String retorno = null;

		if (matricula == null || codigoVinculo == null) {
			throw new IllegalArgumentException("Argumentos obrigatórios.");
		}

		if (!super.isHCPA() || !isOracle()) {
			return "";
		}

		final String storedProcedureName = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.RAPC_FUNCAO_CRACHA;
		//
		final List<String> result = new ArrayList<String>();
		
		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {
					@Override
					public void executeAghProcedure(
							final Connection connection)
							throws SQLException {

						CallableStatement statement = null;
						try {
							final StringBuilder call = new StringBuilder(
									"{? = call ");
							call.append(storedProcedureName);
							call.append("(?,?)}");

							statement = connection.prepareCall(call
									.toString());

							statement.registerOutParameter(1,
									java.sql.Types.VARCHAR);
							statement.setInt(2, matricula);
							statement.setShort(3, codigoVinculo);

							statement.execute();

							result.add(statement.getString((1)));

						} finally {
							if (statement != null) {
								statement.close();
							}
						}
					}
				};
		try {

			super.doWork(work);
			retorno = result.get(0);
		} catch (final Exception e) {
			throwExceptionStoredProcedure(storedProcedureName, e, matricula,
					codigoVinculo);
		}

		if (work.getException() != null) {
			throwExceptionStoredProcedure(storedProcedureName,
					work.getException(), matricula, codigoVinculo);
		}
		
		return retorno;
	}

	/**
	 * ORADB: Function RAPC_BUSCA_OCUPACAO
	 * 
	 * @param codigoOcupacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String buscarDescricaoOcupacao(final Integer codigoOcupacao,
			RapServidores servidorLogado) throws ApplicationBusinessException {
		
		String retorno = "";

		if (codigoOcupacao == null) {
			throw new IllegalArgumentException("Argumento obrigatório.");
		}

		if (!super.isHCPA() || !isOracle()) {
			return "";
		}

		final String storedProcedureName = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.RAPC_BUSCA_OCUPACAO;
		//
		final List<String> result = new ArrayList<String>();
		
		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {
					@Override
					public void executeAghProcedure(
							final Connection connection)
							throws SQLException {

						CallableStatement statement = null;
						try {
							final StringBuilder call = new StringBuilder(
									"{? = call ");
							call.append(storedProcedureName);
							call.append("(?)}");

							statement = connection.prepareCall(call
									.toString());

							statement.registerOutParameter(1,
									java.sql.Types.VARCHAR);
							statement.setInt(2, codigoOcupacao);

							statement.execute();

							result.add(statement.getString((1)));

						} finally {
							if (statement != null) {
								statement.close();
							}
						}
					}
				};
		try {

			super.doWork(work);
			retorno = result.get(0);
		} catch (final Exception e) {
			throwExceptionStoredProcedure(storedProcedureName, e,
					codigoOcupacao);
		}
		
		if (work.getException() != null){
			throwExceptionStoredProcedure(storedProcedureName, work.getException(),
					codigoOcupacao);
		}
		
		
		return retorno;
	}

	/**
	 * ORADB: Function RN_SERC_VER_PERM
	 * 
	 * @param codigoVinculo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean permiteAlterarGPPG(final Short codigoVinculo,
			final Integer codigoOcupacao, RapServidores servidorLogado)
			throws ApplicationBusinessException {

		if (codigoVinculo == null || codigoOcupacao == null) {
			throw new IllegalArgumentException("Argumentos são obrigatórios.");
		}

		if (!super.isHCPA() || !isOracle()) {
			return true;
		}

		//
		final List<Boolean> result = new ArrayList<Boolean>();
		
		AghWork work = 
				new AghWork(servidorLogado != null ? servidorLogado
						.getUsuario() : null) {
					@Override
					public void executeAghProcedure(final Connection connection)
							throws SQLException {

						CallableStatement statement = null;
						try {
							final StringBuilder call = new StringBuilder(
									"{? = call ");

							call.append(EsquemasOracleEnum.AGH.toString());
							call.append('.');
							call.append(ObjetosBancoOracleEnum.RN_SERC_VER_PERM
									.toString());
							call.append("(?,?)}");

							statement = connection.prepareCall(call.toString());
							statement.registerOutParameter(1,
									java.sql.Types.VARCHAR);
							statement.setInt(2, codigoVinculo);
							statement.setInt(3, codigoOcupacao);
							statement.execute();

							final String retorno = statement.getString(1);
							//
							result.add(retorno.equals("S"));
						} finally {
							if (statement != null) {
								statement.close();
							}
						}
					}
				};

		//
		super.doWork(work);
		
		
		if (work.getException() != null){
			throw new HibernateException(work.getException());
		}
		
		return result.get(0);
	}

	/**
	 * Verifica se é o banco de produção
	 * 
	 * @return Verdadeiro se for o banco de produção do HCPA
	 */
	public Boolean isProducaoHCPA() {

		final List<String> result = new ArrayList<String>();
		super.doWork(new Work() {
			@Override
			public void execute(final Connection connection)
					throws SQLException {
				final String url = connection.getMetaData().getURL()
						.toLowerCase();
				if (url.indexOf("hcpa") > -1) {
					result.add("HCPA");
				}
			}
		});

		return result.size() > 0 && result.get(0).equals("HCPA");
	}

	/**
	 * Trata exceptions de chamadas a stored procedures.
	 * 
	 * @param storedProcedureName
	 *            nome da stored procedure
	 * @param e
	 *            exception
	 * @param parametros
	 *            parâmetros fornecidos na chamada
	 * @throws ApplicationBusinessException
	 */
	private void throwExceptionStoredProcedure(
			final String storedProcedureName, final Exception e,
			final Object... parametros) throws ApplicationBusinessException {
		final String string = CoreUtil
				.configurarValoresParametrosCallableStatement(parametros);
		LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(
				storedProcedureName, e, true, string), e);
		throw new ApplicationBusinessException(
				AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
				storedProcedureName, string, CoreUtil
						.configurarMensagemUsuarioLogCallableStatement(
								storedProcedureName, e, false, string));
	}

	/**
	 * @ORADB AGHK_ATD_RN.RN_ATDP_GRAVA_MFP
	 * 
	 * @param seq
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
	public void inserirMvtoFatorPredisponente(final Integer seq,
			final Integer newPacCodigo, final String newLeitoId,
			final Short newQtoNum, final Short newUnfSeq,
			final java.util.Date newDthrInicio,
			final java.util.Date newDthrFim, final Integer newMatricula,
			final Short newVinCodigo, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_GRAVA_MFP
							.toString();

			AghWork work = new AghWork(
					servidorLogado != null ? servidorLogado.getUsuario() : null) {

				@Override
				public void executeAghProcedure(final Connection connection)
						throws SQLException {

					CallableStatement statement = null;
					try {
						final StringBuilder call = new StringBuilder(50).append("{call ");
						call.append(storedProcedureName);
						call.append("(?,?,?,?,?,?,?,?,?)}");

						statement = connection.prepareCall(call.toString());

						CoreUtil.configurarParametroCallableStatement(
								statement, 1, Types.INTEGER, seq);
						CoreUtil.configurarParametroCallableStatement(
								statement, 2, Types.INTEGER, newPacCodigo);
						CoreUtil.configurarParametroCallableStatement(
								statement, 3, Types.VARCHAR, newLeitoId);
						CoreUtil.configurarParametroCallableStatement(
								statement, 4, Types.INTEGER, newQtoNum);
						CoreUtil.configurarParametroCallableStatement(
								statement, 5, Types.INTEGER, newUnfSeq);
						CoreUtil.configurarParametroCallableStatement(
								statement, 6, Types.TIMESTAMP, newDthrInicio);
						CoreUtil.configurarParametroCallableStatement(
								statement, 7, Types.TIMESTAMP, newDthrFim);
						CoreUtil.configurarParametroCallableStatement(
								statement, 8, Types.INTEGER, newMatricula);
						CoreUtil.configurarParametroCallableStatement(
								statement, 9, Types.INTEGER, newVinCodigo);

						statement.execute();

					} finally {
						if (statement != null) {
							statement.close();
						}
					}

				}
			};

			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, seq,
						newPacCodigo, newLeitoId, newQtoNum, newUnfSeq,
						newDthrInicio, newDthrFim, newMatricula, newVinCodigo);
			}
			if (work.getException() != null) {
				throwExceptionStoredProcedure(storedProcedureName,
						work.getException(), seq, newPacCodigo, newLeitoId,
						newQtoNum, newUnfSeq, newDthrInicio, newDthrFim,
						newMatricula, newVinCodigo);
			}
		}
	}

	public void atualizarOrigemCirurgia(final String newOrigem, final Integer newSeq, final Integer newPacCodigo,
			final java.util.Date newDthrInicio, RapServidores servidorLogado) throws ApplicationBusinessException {
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_ATU_CIR_UPD.toString();

			AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {

				@Override
				public void executeAghProcedure(final Connection connection) throws SQLException {

					CallableStatement statement = null;
					try {
						final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?)}");

						statement = connection.prepareCall(call.toString());

						CoreUtil.configurarParametroCallableStatement(statement, 1, Types.VARCHAR, newOrigem);
						CoreUtil.configurarParametroCallableStatement(statement, 2, Types.INTEGER, newSeq);
						CoreUtil.configurarParametroCallableStatement(statement, 3, Types.INTEGER, newPacCodigo);
						CoreUtil.configurarParametroCallableStatement(statement, 4, Types.TIMESTAMP, newDthrInicio);

						statement.execute();

					} finally {
						if (statement != null) {
							statement.close();
						}
					}

				}
			};

			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, newOrigem, newSeq, newPacCodigo, newDthrInicio);
			}
			if (work.getException() != null) {
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), newOrigem, newSeq, newPacCodigo, newDthrInicio);
			}
		}
	}

	public void cancelarExamesAmbulatorio(final Integer atdSeq,
			final DominioPacAtendimento indPacAtendimento,
			final RapServidores servidorLogado) throws ApplicationBusinessException {

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_CA_EXME_AMB
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER, atdSeq);
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.VARCHAR,
										indPacAtendimento.toString());

								statement.execute();

							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, atdSeq,
						indPacAtendimento.toString());
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException() , atdSeq,
						indPacAtendimento.toString());
			}
		}
	}

	public void atualizarPacientesAtendProf(final Integer atdSeq,
			final RapServidores servidorLogado) throws ApplicationBusinessException {

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_ATU_PAC_PROF
							.toString();
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append("(?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER, atdSeq);

								statement.execute();

							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, atdSeq);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq);
			}
		}
	}

	public void atualizarSumariaAlta(final Integer newAtdSeq, final String newIndSitSumAlta,
			final String oldIndSitSumAlta, final String newControleSumAlta,
			final String newOrigem, final Integer newIntSeq, final Integer newAtuSeq, final Integer newHodSeq,
			final Integer newPacCodigo, final Short newUnfSeq, final Short newEspSeq, final Integer newAtdSeqMae, final RapServidores servidorLogado)
			throws ApplicationBusinessException {

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_ATU_ALTA_SUM.toString();
			AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {

				@Override
				public void executeAghProcedure(final Connection connection) throws SQLException {

					CallableStatement statement = null;
					try {
						final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?,?,?,?,?,?,?,?,?)}");

						statement = connection.prepareCall(call.toString());

						CoreUtil.configurarParametroCallableStatement(statement, 1, Types.INTEGER, newAtdSeq);
						CoreUtil.configurarParametroCallableStatement(statement, 2, Types.VARCHAR, newIndSitSumAlta);
						CoreUtil.configurarParametroCallableStatement(statement, 3, Types.VARCHAR, oldIndSitSumAlta);
						CoreUtil.configurarParametroCallableStatement(statement, 4, Types.VARCHAR, newControleSumAlta);
						CoreUtil.configurarParametroCallableStatement(statement, 5, Types.VARCHAR, newOrigem);
						CoreUtil.configurarParametroCallableStatement(statement, 6, Types.INTEGER, newIntSeq);
						CoreUtil.configurarParametroCallableStatement(statement, 7, Types.INTEGER, newAtuSeq);
						CoreUtil.configurarParametroCallableStatement(statement, 8, Types.INTEGER, newHodSeq);
						CoreUtil.configurarParametroCallableStatement(statement, 9, Types.INTEGER, newPacCodigo);
						CoreUtil.configurarParametroCallableStatement(statement, 10, Types.INTEGER, newUnfSeq);
						CoreUtil.configurarParametroCallableStatement(statement, 11, Types.INTEGER, newEspSeq);
						CoreUtil.configurarParametroCallableStatement(statement, 12, Types.INTEGER, newAtdSeqMae);

						statement.execute();

					} finally {
						if (statement != null) {
							statement.close();
						}
					}

				}
			};
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, newAtdSeq, newIndSitSumAlta, oldIndSitSumAlta,
						newControleSumAlta, newOrigem, newIntSeq, newAtuSeq, newHodSeq, newPacCodigo, newUnfSeq, newEspSeq,
						newAtdSeqMae);
			}

			if (work.getException() != null) {
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), newAtdSeq, newIndSitSumAlta,
						oldIndSitSumAlta, newControleSumAlta, newOrigem, newIntSeq, newAtuSeq, newHodSeq,
						newPacCodigo, newUnfSeq, newEspSeq, newAtdSeqMae);
			}
		}
	}

	/**
	 * @ORADB Procedure AGHK_ATD_RN.RN_ATDP_ATU_LIST_PAC
	 */
	public void atualizarListaPacientes(final DominioOrigemAtendimento origem, final java.util.Date dataFim, final Integer seqAtendimento,
			final Short seqUnidadeFuncional, final Short seqEspecialidade, final Integer matricula, final Short vinCodigo,
			final DominioSituacaoSumarioAlta situacaoSumarioAlta, RapServidores servidorLogado) throws ApplicationBusinessException {
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_ATU_LIST_PAC.toString();

			AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {

				@Override
				public void executeAghProcedure(final Connection connection) throws SQLException {

					CallableStatement statement = null;
					try {
						final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?,?,?,?,?)}");

						statement = connection.prepareCall(call.toString());

						CoreUtil.configurarParametroCallableStatement(statement, 1, Types.VARCHAR, origem != null ? origem.toString() : null);
						CoreUtil.configurarParametroCallableStatement(statement, 2, Types.TIMESTAMP, dataFim);
						CoreUtil.configurarParametroCallableStatement(statement, 3, Types.INTEGER, seqAtendimento);
						CoreUtil.configurarParametroCallableStatement(statement, 4, Types.INTEGER, seqUnidadeFuncional);
						CoreUtil.configurarParametroCallableStatement(statement, 5, Types.INTEGER, seqEspecialidade);
						CoreUtil.configurarParametroCallableStatement(statement, 6, Types.INTEGER, matricula);
						CoreUtil.configurarParametroCallableStatement(statement, 7, Types.INTEGER, vinCodigo);
						CoreUtil.configurarParametroCallableStatement(statement, 8, Types.VARCHAR, situacaoSumarioAlta != null ? situacaoSumarioAlta.toString() : null);

						statement.execute();

					} finally {
						if (statement != null) {
							statement.close();
						}
					}

				}
			};

			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, origem, dataFim, seqAtendimento, seqUnidadeFuncional, seqEspecialidade,
						matricula, vinCodigo, situacaoSumarioAlta);
			}

			if (work.getException() != null) {
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), origem, dataFim, seqAtendimento, seqUnidadeFuncional,
						seqEspecialidade, matricula, vinCodigo, situacaoSumarioAlta);
			}
		}
	}

	public void inserirOrdemLocalizacao(final Integer atdSeq,
			final java.util.Date ontem, final Short seqUnidadeFuncional,
			final Short newQtoNum, final String newLeitoId,
			final RapServidores servidorLogado) throws ApplicationBusinessException {
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.ECEP_INSERE_ORDEM_LOCALIZACAO
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?,?,?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER, atdSeq);
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.TIMESTAMP, ontem);
								CoreUtil.configurarParametroCallableStatement(
										statement, 3, Types.INTEGER,
										seqUnidadeFuncional);
								CoreUtil.configurarParametroCallableStatement(
										statement, 4, Types.INTEGER,
										newQtoNum);
								CoreUtil.configurarParametroCallableStatement(
										statement, 5, Types.VARCHAR,
										newLeitoId);

								statement.execute();

							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, atdSeq,
						ontem, seqUnidadeFuncional, newQtoNum, newLeitoId);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq,
						ontem, seqUnidadeFuncional, newQtoNum, newLeitoId);
			}
		}
	}

	public void alteraOrdemLocalizacao(final Integer atdSeq,
			final java.util.Date dtReferencia, final Short unfSeqOld,
			final Short unfSeqNew, final Short newQtoNum,
			final String newLeitoId, final RapServidores servidorLogado)
			throws ApplicationBusinessException {
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.ECEP_ALTERA_ORDEM_LOCALIZACAO
							.toString();
			
			AghWork work = new AghWork(servidorLogado != null ? servidorLogado
					.getUsuario() : null) {

				@Override
				public void executeAghProcedure(
						final Connection connection)
						throws SQLException {

					CallableStatement statement = null;
					try {
						final StringBuilder call = new StringBuilder(
								"{call ").append(
								storedProcedureName).append(
								"(?,?,?,?,?,?)}");

						statement = connection.prepareCall(call
								.toString());

						CoreUtil.configurarParametroCallableStatement(
								statement, 1, Types.INTEGER, atdSeq);
						CoreUtil.configurarParametroCallableStatement(
								statement, 2, Types.TIMESTAMP,
								dtReferencia);
						CoreUtil.configurarParametroCallableStatement(
								statement, 3, Types.INTEGER,
								unfSeqOld);
						CoreUtil.configurarParametroCallableStatement(
								statement, 4, Types.INTEGER,
								unfSeqNew);
						CoreUtil.configurarParametroCallableStatement(
								statement, 5, Types.INTEGER,
								newQtoNum);
						CoreUtil.configurarParametroCallableStatement(
								statement, 6, Types.VARCHAR,
								newLeitoId);

						statement.execute();

					} finally {
						if (statement != null) {
							statement.close();
						}
					}

				}
			};
			
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, atdSeq,
						dtReferencia, unfSeqOld, unfSeqNew, newQtoNum,
						newLeitoId);
			}
			
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq,
						dtReferencia, unfSeqOld, unfSeqNew, newQtoNum,
						newLeitoId);
			}
		}
	}

	public void gerarDiagnosticoCti(final Integer seqAtendimento,
			final Short seqUnidadeFuncional,
			final java.util.Date dataIngressoUnidade,
			RapServidores servidorLogado) throws ApplicationBusinessException {

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_GERA_DIAG_CT
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER,
										seqAtendimento);
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.INTEGER,
										seqUnidadeFuncional);
								CoreUtil.configurarParametroCallableStatement(
										statement, 3, Types.TIMESTAMP,
										dataIngressoUnidade);

								statement.execute();

							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e,
						seqAtendimento, seqUnidadeFuncional,
						dataIngressoUnidade);
			}
			
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(),
						seqAtendimento, seqUnidadeFuncional,
						dataIngressoUnidade);
			}
		}
	}

	public void gerarFichaApache(final Integer seqAtendimento,
			final Short seqUnidadeFuncional,
			final java.util.Date dataIngressoUnidade,
			RapServidores servidorLogado) throws ApplicationBusinessException {

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_GERA_APACHE
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER,
										seqAtendimento);
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.INTEGER,
										seqUnidadeFuncional);
								CoreUtil.configurarParametroCallableStatement(
										statement, 3, Types.TIMESTAMP,
										dataIngressoUnidade);

								statement.execute();

							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e,
						seqAtendimento, seqUnidadeFuncional,
						dataIngressoUnidade);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(),
						seqAtendimento, seqUnidadeFuncional,
						dataIngressoUnidade);
			}
		}
	}

	public void atualizarFichaApache(final Integer seqAtendimento,
			final RapServidores servidorLogado) throws ApplicationBusinessException {

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_ATU_APACHE
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append("(?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER,
										seqAtendimento);

								statement.execute();

							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e,
						seqAtendimento);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(),
						seqAtendimento);
			}
		}
	}

	public void atualizarCodigoPacienteProcedimentoRealizado(
			final Integer newSeq, final Integer newPacCodigo,
			RapServidores servidorLogado) throws ApplicationBusinessException {
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.FATK_PMR_RN_RN_PMRP_ATU_PAC_COD
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER, newSeq);
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.INTEGER,
										newPacCodigo);

								statement.execute();

							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, newSeq,
						newPacCodigo);
			}
			
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), newSeq,
						newPacCodigo);
			}

		}
	}

	public void gerarOrdemAdministracaoPrescricao(final Integer newSeq,
			final java.util.Date newDthrInicio, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH + "."
					+ ObjetosBancoOracleEnum.MPMP_GERA_ECE_QUIMIO.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER, newSeq);
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.TIMESTAMP,
										newDthrInicio);

								statement.execute();

							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, newSeq,
						newDthrInicio);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), newSeq,
						newDthrInicio);
			}
		}
	}

	public void atualizarTransfAGHOS(final Integer newPacCodigo,
			final String oldLeitoId, final String newLeitoId,
			final Integer newProntuario, final Short newQtoNum,
			final Short newUnfSeq, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_2_RN_ATDP_TRANSF_AGHOS
							.toString();
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?,?,?,?,?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER,
										newPacCodigo); // P_NEW_PAC_CODIGO
														// IN NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.VARCHAR,
										oldLeitoId); // P_OLD_LTO_ID IN
														// VARCHAR2
								CoreUtil.configurarParametroCallableStatement(
										statement, 3, Types.VARCHAR,
										newLeitoId); // P_NEW_LTO_ID IN
														// VARCHAR2
								CoreUtil.configurarParametroCallableStatement(
										statement, 4, Types.INTEGER,
										newProntuario); // P_NEW_PRONTUARIO
														// IN NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 5, Types.VARCHAR,
										newLeitoId); // P_NEW_LTO_LTO_ID IN
														// VARCHAR2
								CoreUtil.configurarParametroCallableStatement(
										statement, 6, Types.INTEGER,
										newQtoNum); // P_NEW_QRT_NUMERO IN
													// NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 7, Types.INTEGER,
										newUnfSeq); // P_NEW_UNF_SEQ IN
													// NUMBER

								statement.execute();

							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e,
						newPacCodigo, oldLeitoId, newLeitoId, newProntuario,
						newQtoNum, newUnfSeq);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(),
						newPacCodigo, oldLeitoId, newLeitoId, newProntuario,
						newQtoNum, newUnfSeq);
			}
		}
	}

	public void atualizarAltaAGHOS(final Integer newPacCodigo,
			final java.util.Date oldDthrFim, final java.util.Date newDthrFim,
			final Integer newProntuario, final String newLeitoId,
			final Short newQtoNum, final Short newUnfSeq,
			RapServidores servidorLogado) throws ApplicationBusinessException {
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_2_RN_ATDP_ALTA_AGHOS
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?,?,?,?,?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER,
										newPacCodigo); // P_NEW_PAC_CODIGO
														// IN NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.TIMESTAMP,
										oldDthrFim); // P_OLD_DTHR_FIM IN
														// DATE
								CoreUtil.configurarParametroCallableStatement(
										statement, 3, Types.TIMESTAMP,
										newDthrFim); // P_NEW_DTHR_FIM DATE
								CoreUtil.configurarParametroCallableStatement(
										statement, 4, Types.INTEGER,
										newProntuario); // P_NEW_PRONTUARIO
														// IN NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 5, Types.VARCHAR,
										newLeitoId); // P_NEW_LTO_LTO_ID IN
														// VARCHAR2
								CoreUtil.configurarParametroCallableStatement(
										statement, 6, Types.INTEGER,
										newQtoNum); // P_NEW_QRT_NUMERO IN
													// NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 7, Types.INTEGER,
										newUnfSeq); // P_NEW_UNF_SEQ IN
													// NUMBER

								statement.execute();

							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e,
						newPacCodigo, oldDthrFim, newDthrFim, newLeitoId,
						newProntuario, newQtoNum, newUnfSeq);
			}
			
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(),
						newPacCodigo, oldDthrFim, newDthrFim, newLeitoId,
						newProntuario, newQtoNum, newUnfSeq);
			}
		}
	}

	public void atualizarInformacoesFormularioPim2(final String oper,
			final DominioOrigemAtendimento origem, final Integer atdSeq,
			final Short oldUnfSeq, final Short newUnfSeq,
			final java.util.Date oldDthrFim, final java.util.Date newDthrFim,
			final java.util.Date dthrIngressoUnidade,
			final RapServidores servidorLogado) throws ApplicationBusinessException {

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_2_RN_ATDP_ATU_PIM2
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?,?,?,?,?,?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.VARCHAR, oper);
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.VARCHAR,
										origem != null ? origem.toString() : null);
								CoreUtil.configurarParametroCallableStatement(
										statement, 3, Types.INTEGER, atdSeq);
								CoreUtil.configurarParametroCallableStatement(
										statement, 4, Types.INTEGER,
										oldUnfSeq);
								CoreUtil.configurarParametroCallableStatement(
										statement, 5, Types.INTEGER,
										newUnfSeq);
								CoreUtil.configurarParametroCallableStatement(
										statement, 6, Types.TIMESTAMP,
										oldDthrFim);
								CoreUtil.configurarParametroCallableStatement(
										statement, 7, Types.TIMESTAMP,
										newDthrFim);
								CoreUtil.configurarParametroCallableStatement(
										statement, 8, Types.TIMESTAMP,
										dthrIngressoUnidade);

								statement.execute();
							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, oper,
						origem, atdSeq, oldUnfSeq, newUnfSeq,
						oldDthrFim, newDthrFim, dthrIngressoUnidade);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), oper,
						origem, atdSeq, oldUnfSeq, newUnfSeq,
						oldDthrFim, newDthrFim, dthrIngressoUnidade);
			}
		}
	}

	public void atualizarCirurgiaAtendimento(
			final DominioOrigemAtendimento newOrigem, final Integer newSeq,
			final Integer newPacCodigo, final java.util.Date newDthrInicio,
			RapServidores servidorLogado) throws ApplicationBusinessException {
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_ATU_CIRURGIA
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?,?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.VARCHAR,
										newOrigem.toString()); // P_NEW_ORIGEM
																// VARCHAR2
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.INTEGER, newSeq); // P_NEW_ATD_SEQ
																				// NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 3, Types.INTEGER,
										newPacCodigo); // P_NEW_PAC_CODIGO
														// NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 4, Types.TIMESTAMP,
										newDthrInicio); // P_NEW_DTHR_INICIO
														// IN DATE

								statement.execute();

							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
					
					
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, newSeq,
						newPacCodigo, newDthrInicio);
			}
			
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), newSeq,
						newPacCodigo, newDthrInicio);
			}
		}
	}

	public void atualizaAgendaControleDispensacaoAtendimento(
			final Integer atdSeq, final String tipoOperacao,
			final Integer codigoPacienteNovo,
			final DominioOrigemAtendimento oldOrigem,
			final DominioOrigemAtendimento newOrigem,
			final java.util.Date dataInicioNova,
			final java.util.Date dataFimAntiga,
			final java.util.Date dataFimNova, final Short unfSeqAntiga,
			final Short unfSeqNova, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_ATU_AGEN_TRP
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?,?,?,?,?,?,?,?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.VARCHAR,
										tipoOperacao); // P_OPERACAO IN
														// VARCHAR2
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.INTEGER,
										codigoPacienteNovo); // P_NEW_PAC_CODIGO
																// IN NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 3, Types.VARCHAR,
										oldOrigem != null ? oldOrigem.toString() : null); // P_OLD_ORIGEM
																// IN
																// VARCHAR2
								CoreUtil.configurarParametroCallableStatement(
										statement, 4, Types.VARCHAR,
										newOrigem != null ? newOrigem.toString() : null); // P_NEW_ORIGEM
																// IN
																// VARCHAR2
								CoreUtil.configurarParametroCallableStatement(
										statement, 5, Types.TIMESTAMP,
										dataInicioNova); // P_NEW_DTHR_INICIO
															// IN DATE
								CoreUtil.configurarParametroCallableStatement(
										statement, 6, Types.TIMESTAMP,
										dataFimAntiga); // P_OLD_DTHR_FIM IN
														// DATE
								CoreUtil.configurarParametroCallableStatement(
										statement, 7, Types.TIMESTAMP,
										dataFimNova); // P_NEW_DTHR_FIM IN
														// DATE
								CoreUtil.configurarParametroCallableStatement(
										statement, 8, Types.INTEGER,
										unfSeqAntiga); // P_OLD_UNF_SEQ IN
														// NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 9, Types.INTEGER,
										unfSeqNova); // P_NEW_UNF_SEQ IN
														// NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 10, Types.INTEGER,
										atdSeq); // P_ATD_SEQ IN number

								statement.execute();

							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, atdSeq,
						tipoOperacao, codigoPacienteNovo, oldOrigem, newOrigem,
						dataInicioNova, dataFimAntiga, dataFimNova,
						unfSeqAntiga, unfSeqNova);
			}
			
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq,
						tipoOperacao, codigoPacienteNovo, oldOrigem, newOrigem,
						dataInicioNova, dataFimAntiga, dataFimNova,
						unfSeqAntiga, unfSeqNova);
			}
		}
	}

	/**
	 * @ORADB chamada nativa AINK_INT_RN.RN_INTP_ATU_ALT_CNTA
	 * 
	 * @param seqInternacao
	 * @param dthrAltaMedica
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void executaAtualizacaoContaHospitalarAlta(
			final Integer seqInternacao, final java.util.Date dthrAltaMedica,
			final RapServidores servidorLogado) throws ApplicationBusinessException {

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AINK_INT_RN_RN_INTP_ATU_ALT_CNTA
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER,
										seqInternacao); // P_INT_SEQ IN
														// NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.TIMESTAMP,
										dthrAltaMedica); // P_DTHR_ALTA_MEDICA
															// IN DATE

								statement.execute();
							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
				LOG.info("Executou por chamada nativa AINK_INT_RN.RN_INTP_ATU_ALT_CNTA");
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e,
						seqInternacao, dthrAltaMedica);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(),
						seqInternacao, dthrAltaMedica);
			}
		}

	}

	/**
	 * @ORADB chamada nativa AGHK_ATD_RN.RN_ATDP_CANC_EXME
	 * 
	 *        Cancela exames ainda não realizados quando da alta do paciente
	 * 
	 * @param atdSeq
	 * @param tipoAlta
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void executaCancelaExamesAlta(final Integer atdSeq,
			final String tipoAlta, final RapServidores servidorLogado)
			throws ApplicationBusinessException {

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_CANC_EXME
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER, atdSeq); // P_ATD_SEQ
																				// IN
																				// NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.VARCHAR,
										tipoAlta); // P_TAM_CODIGO IN
													// VARCHAR2

								statement.execute();
							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
				LOG.info("Executou por chamada nativa AGHK_ATD_RN.RN_ATDP_CANC_EXME");
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, atdSeq,
						tipoAlta);
			}

			if (work.getException() != null) {
				throwExceptionStoredProcedure(storedProcedureName,
						work.getException(), atdSeq, tipoAlta);
			}
		}

	}

	/**
	 * @ORADB chamada nativa AGHK_ATD_RN.RN_ATDP_CANC_AMOSTRA
	 * 
	 *        Cancela amostras hemoterápicas não coletadas quando da alta
	 *        paciente
	 * 
	 * @param atdSeq
	 * @param tipoAlta
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void executaCancelaAmostrasHemoterapicas(final Integer atdSeq,
			final String tipoAlta, final RapServidores servidorLogado)
			throws ApplicationBusinessException {

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_CANC_AMOSTRA
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER, atdSeq); // P_ATD_SEQ
																				// IN
																				// NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.VARCHAR,
										tipoAlta); // P_TAM_CODIGO IN
													// VARCHAR2

								statement.execute();
							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
				LOG.info("Executou por chamada nativa AGHK_ATD_RN.RN_ATDP_CANC_AMOSTRA");
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, atdSeq,
						tipoAlta);
			}
			
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq,
						tipoAlta);
			}
		}

	}

	/**
	 * @ORADB chamada nativa AINK_INT_RN.RN_INTP_ATU_INT_CNTA
	 * 
	 * @param seqInternacao
	 * @param dthrInternacao
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void executaAtualizarContaInternacao(final Integer seqInternacao,
			final java.util.Date dthrInternacao,
			final RapServidores servidorLogado) throws ApplicationBusinessException {

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH
					+ "."
					+ ObjetosBancoOracleEnum.AINK_INT_RN_RN_INTP_ATU_INT_CNTA
							.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER,
										seqInternacao); // P_INT_SEQ IN
														// NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.TIMESTAMP,
										dthrInternacao); // P_DTHR_INT IN
															// DATE

								statement.execute();
							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
				LOG.info("Executou por chamada nativa AINK_INT_RN.RN_INTP_ATU_INT_CNTA");
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e,
						seqInternacao, dthrInternacao);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(),
						seqInternacao, dthrInternacao);
			}
		}

	}

	/**
	 * @ORADB chamada nativa MPMP_GERA_LAUDO_AIN
	 * 
	 * @param atdSeq
	 * @param dthrInicio
	 * @param tipoLaudo
	 * @param cspCnvCodigo
	 * @param cspSeq
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void executaGeraLaudoInternacao(final Integer atdSeq,
			final java.util.Date dthrInicio, final String tipoLaudo,
			final Short cspCnvCodigo, final Byte cspSeq,
			final RapServidores servidorLogado) throws ApplicationBusinessException {

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH + "."
					+ ObjetosBancoOracleEnum.MPMP_GERA_LAUDO_AIN.toString();
			
			AghWork work = 
					new AghWork(servidorLogado != null ? servidorLogado
							.getUsuario() : null) {

						@Override
						public void executeAghProcedure(
								final Connection connection)
								throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder(
										"{call ").append(
										storedProcedureName).append(
										"(?,?,?,?,?)}");

								statement = connection.prepareCall(call
										.toString());

								CoreUtil.configurarParametroCallableStatement(
										statement, 1, Types.INTEGER, atdSeq); // P_ATD_SEQ
																				// IN
																				// NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 2, Types.TIMESTAMP,
										dthrInicio); // P_DTHR_INICIO IN
														// DATE
								CoreUtil.configurarParametroCallableStatement(
										statement, 3, Types.VARCHAR,
										tipoLaudo); // P_TIPO_LAUDO IN
													// VARCHAR2
								CoreUtil.configurarParametroCallableStatement(
										statement, 4, Types.INTEGER,
										cspCnvCodigo); // P_CSP_CNV_CODIGO
														// IN NUMBER
								CoreUtil.configurarParametroCallableStatement(
										statement, 5, Types.INTEGER, cspSeq); // P_CSP_SEQ
																				// IN
																				// NUMBER

								statement.execute();
							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
				LOG.info("Executou por chamada nativa MPMP_GERA_LAUDO_AIN");
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, atdSeq,
						dthrInicio, tipoLaudo, cspCnvCodigo, cspSeq);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq,
						dthrInicio, tipoLaudo, cspCnvCodigo, cspSeq);
			}
		}

	}

	/**
	 * @ORADB chamada nativa AGHK_ATD_RN.RN_ATDP_VER_FIM_SUM
	 * 
	 * @param origem
	 * @param dthrFim
	 * @param seqAtd
	 * @param indSitSumarioAlta
	 * @param seqUnidFunc
	 * @param ctrlSumrAltaPendente
	 * @param seqAtdUrg
	 * @param nomeMicrocomputador
	 * @param servidorLogado 
	 * @throws ApplicationBusinessException 
	 */
	public void executaVerificarPerfilSumario(final String origem,
			final java.util.Date dthrFim, final Integer seqAtd,
			final String indSitSumarioAlta, final Short seqUnidFunc,
			final String ctrlSumrAltaPendente,
			final Integer seqAtdUrg, final String nomeMicrocomputador,
			final RapServidores servidorLogado) throws ApplicationBusinessException {
		

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_VER_FIM_SUM.toString();
			
			AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {

						@Override
						public void executeAghProcedure(final Connection connection) throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?,?,?,?)}");

								statement = connection.prepareCall(call	.toString());
								CoreUtil.configurarParametroCallableStatement(statement, 1, Types.VARCHAR, origem); // P_NEW_ORIGEM IN VARCHAR2
								CoreUtil.configurarParametroCallableStatement(statement, 2, Types.TIMESTAMP, dthrFim); // P_NEW_DTHR_FIM IN DATE
								CoreUtil.configurarParametroCallableStatement(statement, 3, Types.INTEGER, seqAtd); // P_NEW_ATD_SEQ NUMBER
								CoreUtil.configurarParametroCallableStatement(statement, 4, Types.VARCHAR, indSitSumarioAlta); // P_NEW_IND_SIT_SUMARIO_ALTA IN OUT VARCHAR2
								CoreUtil.configurarParametroCallableStatement(statement, 5, Types.INTEGER, seqUnidFunc); // P_NEW_UNF_SEQ IN VARCHAR2
								CoreUtil.configurarParametroCallableStatement(statement, 6, Types.VARCHAR, ctrlSumrAltaPendente); // P_NEW_CTRL_SUMR_ALTA IN OUT VARCHAR2
								CoreUtil.configurarParametroCallableStatement(statement, 7, Types.INTEGER, seqAtdUrg); // P_NEW_ATU_SEQ IN NUMBER

								statement.execute();
							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
				LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_VER_FIM_SUM.toString());
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, origem, dthrFim, seqAtd, indSitSumarioAlta, seqUnidFunc, ctrlSumrAltaPendente, seqAtdUrg);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), dthrFim, seqAtd, indSitSumarioAlta, seqUnidFunc, ctrlSumrAltaPendente, seqAtdUrg, origem);
			}
		}
	}
	

	/**
	 * @ORADB chamada nativa FATK_PMR_RN.RN_PMRP_TRC_ESP_ATD
	 * 
	 * @param atdSeq
	 * @param espSeq
	 * @param nomeMicrocomputador
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void executaAtualizacaoContaHospitFatAmbulatorio(final Integer atdSeq,
			final Short espSeq, final String nomeMicrocomputador,
			final RapServidores servidorLogado) throws ApplicationBusinessException {
		

		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.FATK_PMR_RN_RN_PMRP_TRC_ESP_ATD.toString();
			
			AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {

						@Override
						public void executeAghProcedure(final Connection connection) throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?)}");

								statement = connection.prepareCall(call	.toString());

								CoreUtil.configurarParametroCallableStatement(statement, 1, Types.INTEGER, atdSeq); // P_ATD_SEQ IN NUMBER
								CoreUtil.configurarParametroCallableStatement(statement, 2, Types.INTEGER, espSeq); // P_ESP_SEQ IN NUMBER

								statement.execute();
							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
				LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.FATK_PMR_RN_RN_PMRP_TRC_ESP_ATD.toString());
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, atdSeq, espSeq);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq, espSeq);
			}
		}
	}

	/**
	 * @ORADB chamada nativa AGHK_ATD_RN.RN_ATDP_ATU_PAC_COD
	 * @param oldPacCodigo
	 * @param newPacCodigo
	 * @param newOrigem
	 * @param newAtdSeq
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	public void executaAtualizarDiagnostico(final Integer oldPacCodigo,
			final Integer newPacCodigo, final DominioOrigemAtendimento newOrigem,
			final Integer newAtdSeq, final RapServidores servidorLogado) throws ApplicationBusinessException {
		
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_ATU_PAC_COD.toString();
			
			AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {

						@Override
						public void executeAghProcedure(final Connection connection) throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?)}");

								statement = connection.prepareCall(call	.toString());

								CoreUtil.configurarParametroCallableStatement(statement, 1, Types.INTEGER, oldPacCodigo); // P_OLD_PAC_CODIGO IN NUMBER
								CoreUtil.configurarParametroCallableStatement(statement, 2, Types.INTEGER, newPacCodigo); // P_NEW_PAC_CODIGO IN NUMBER
								CoreUtil.configurarParametroCallableStatement(statement, 3, Types.VARCHAR, newOrigem != null ? newOrigem.toString() : null); // P_NEW_ORIGEM IN VARCHAR2
								CoreUtil.configurarParametroCallableStatement(statement, 4, Types.INTEGER, newAtdSeq); // P_NEW_ATD_SEQ IN NUMBER

								statement.execute();
							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
				LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_ATU_PAC_COD.toString());
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, oldPacCodigo, newPacCodigo, newOrigem, newAtdSeq);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), oldPacCodigo, newPacCodigo, newOrigem, newAtdSeq);
			}
		}
		
	}
	
	/**
	 * @ORADB chamada nativa AIPK_PAC_ATU.RN_PACP_ATU_NASC
	 * 
	 * @param dtNascimento
	 * @param pacCodigo
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void executaAtualizaDataNascimento(final java.util.Date dtNascimento, final Integer pacCodigo, final RapServidores servidorLogado) throws ApplicationBusinessException {
		
		if (isOracle()) {

			final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AIPK_PAC_ATU_RN_PACP_ATU_NASC.toString();
			
			AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {

						@Override
						public void executeAghProcedure(final Connection connection) throws SQLException {

							CallableStatement statement = null;
							try {
								final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?)}");

								statement = connection.prepareCall(call	.toString());

								CoreUtil.configurarParametroCallableStatement(statement, 1, Types.TIMESTAMP, dtNascimento); // P_DT_NASCIMENTO IN DATE
								CoreUtil.configurarParametroCallableStatement(statement, 2, Types.INTEGER, pacCodigo); // P_PAC_CODIGO    IN NUMBER

								statement.execute();
							} finally {
								if (statement != null) {
									statement.close();
								}
							}

						}
					};
			try {
				super.doWork(work);
				LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.AGHK_ATD_RN_RN_ATDP_ATU_PAC_COD.toString());
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, dtNascimento, pacCodigo);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), dtNascimento, pacCodigo);
			}
		}
		
		
	}

		/**
		 * @ORADB AINK_INT_RN.RN_INTP_ATU_CNV_EXME
		 * 
		 * @param seqAtendimento
		 * @param codigoConvenio
		 * @param seqConvenioSaudePlano
		 * @param data
		 * @param servidorLogado
		 * @throws ApplicationBusinessException
		 */
		public void executaAtualizarConvenioPlanoExames(final Integer seqAtendimento, final Short codigoConvenio,
				final Byte seqConvenioSaudePlano, final java.util.Date data, final RapServidores servidorLogado) throws ApplicationBusinessException {
			
			if (isOracle()) {

				final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AINK_INT_RN_RN_INTP_ATU_CNV_EXME.toString();
				
				AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {

							@Override
							public void executeAghProcedure(final Connection connection) throws SQLException {

								CallableStatement statement = null;
								try {
									final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?)}");

									statement = connection.prepareCall(call	.toString());

									CoreUtil.configurarParametroCallableStatement(statement, 1, Types.INTEGER, seqAtendimento); //P_ATD_SEQ    IN NUMBER
									CoreUtil.configurarParametroCallableStatement(statement, 2, Types.INTEGER, codigoConvenio); //P_CNV_CODIGO IN NUMBER
									CoreUtil.configurarParametroCallableStatement(statement, 3, Types.INTEGER, seqConvenioSaudePlano); //P_CSP_SEQ    IN NUMBER
									CoreUtil.configurarParametroCallableStatement(statement, 4, Types.TIMESTAMP, data); //P_DTHR DATE

									statement.execute();
								} finally {
									if (statement != null) {
										statement.close();
									}
								}

							}
						};
				try {
					super.doWork(work);
					LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.AINK_INT_RN_RN_INTP_ATU_CNV_EXME.toString());
				} catch (final Exception e) {
					throwExceptionStoredProcedure(storedProcedureName, e, seqAtendimento, codigoConvenio, seqConvenioSaudePlano, data);
				}
				if (work.getException() != null){
					throwExceptionStoredProcedure(storedProcedureName, work.getException(), seqAtendimento, codigoConvenio, seqConvenioSaudePlano, data);
				}
			}
		}

		/**
		 * @ORADB AINK_INT_RN.RN_INTP_ATU_CNV_CIRG
		 * 
		 * @param seqAtendimento
		 * @param codigoConvenio
		 * @param seqConvenioSaudePlano
		 * @param data
		 * @param servidorLogado
		 * @throws ApplicationBusinessException
		 */
		public void executaAtualizarConvenioPlanoCirurgias(final Integer seqAtendimento, final Short codigoConvenio, final Byte seqConvenioSaudePlano,
				final java.util.Date data, final RapServidores servidorLogado) throws ApplicationBusinessException {
			
			if (isOracle()) {

				final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AINK_INT_RN_RN_INTP_ATU_CNV_CIRG.toString();
				
				AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {

							@Override
							public void executeAghProcedure(final Connection connection) throws SQLException {

								CallableStatement statement = null;
								try {
									final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?)}");

									statement = connection.prepareCall(call	.toString());

									CoreUtil.configurarParametroCallableStatement(statement, 1, Types.INTEGER, seqAtendimento); //P_ATD_SEQ    IN NUMBER
									CoreUtil.configurarParametroCallableStatement(statement, 2, Types.INTEGER, codigoConvenio); //P_CNV_CODIGO IN NUMBER
									CoreUtil.configurarParametroCallableStatement(statement, 3, Types.INTEGER, seqConvenioSaudePlano); //P_CSP_SEQ    IN NUMBER
									CoreUtil.configurarParametroCallableStatement(statement, 4, Types.TIMESTAMP, data); //P_DTHR DATE

									statement.execute();
								} finally {
									if (statement != null) {
										statement.close();
									}
								}

							}
						};
				try {
					super.doWork(work);
					LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.AINK_INT_RN_RN_INTP_ATU_CNV_CIRG.toString());
				} catch (final Exception e) {
					throwExceptionStoredProcedure(storedProcedureName, e, seqAtendimento, codigoConvenio, seqConvenioSaudePlano, data);
				}
				if (work.getException() != null){
					throwExceptionStoredProcedure(storedProcedureName, work.getException(), seqAtendimento, codigoConvenio, seqConvenioSaudePlano, data);
				}
			}
		}

		/**
		 * @ORADB AIPK_TROCA_CONVENIO_AGHU.EVT_ON_INSERT
		 * 
		 * @param internacaoSeq
		 * @param cnvCodigo
		 * @param seqConv
		 * @param dtIntAdminNovo
		 * @param cthSeqAtual
		 * @param dtIntAdminAtual
		 * @param dtAltaAdminAtual
		 * @param cnvCodigoOld
		 * @param servidorLogado
		 * @throws ApplicationBusinessException
		 */
		public void executaTrocaConvenios(final Integer internacaoSeq, 
				final Short cnvCodigo, 
				final Byte seqConv, 
				final java.util.Date dtIntAdminNovo, 
			 	final Integer cthSeqAtual, 
				final java.util.Date dtIntAdminAtual,
				final java.util.Date dtAltaAdminAtual,
				final Short cnvCodigoOld, 
				final RapServidores servidorLogado) throws ApplicationBusinessException {
	
			final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AIPK_TROCA_CONVENIO_AGHU_EVT_ON_INSERT.toString();
			final String usuarioLogado = servidorLogado != null ? servidorLogado.getUsuario() : null;
			
			AghWork work = new AghWork(usuarioLogado) {
				public void executeAghProcedure(Connection connection) throws SQLException {
					
					CallableStatement cs = null;
					try {
						StringBuilder sbCall = new StringBuilder(50).append("{call ");
						sbCall.append(storedProcedureName);
						sbCall.append("(?,?,?,?,?,?,?,?,?)}");
	
						cs = connection.prepareCall(sbCall.toString());
	
						CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, internacaoSeq); //P_INT_SEQ NUMBER
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, cnvCodigo); //P_CTH_CSP_CNV_CODIGO_NOVO NUMBER
						CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER, seqConv); //P_CTH_CSP_SEQ_NOVO NUMBER						
						CoreUtil.configurarParametroCallableStatement(cs, 4, Types.TIMESTAMP, dtIntAdminNovo); //P_CTH_DT_INT_ADMIN_NOVO date
						CoreUtil.configurarParametroCallableStatement(cs, 5, Types.INTEGER, cthSeqAtual); //P_VCH_CTH_SEQ_ATUAL NUMBER
						CoreUtil.configurarParametroCallableStatement(cs, 6, Types.TIMESTAMP, dtIntAdminAtual); //P_VCH_DT_INT_ADMIN_ATUAL date
						CoreUtil.configurarParametroCallableStatement(cs, 7, Types.TIMESTAMP, dtAltaAdminAtual); //P_VCH_DT_ALTA_ADMIN_ATUAL date
						CoreUtil.configurarParametroCallableStatement(cs, 8, Types.INTEGER, cnvCodigoOld); //P_CTH_CSP_CNV_CODIGO_BANCO
						CoreUtil.configurarParametroCallableStatement(cs, 9, Types.VARCHAR, DominioTipoPlano.I.toString()); //P_CTH_IND_TIPO_PLANO varchar2 => sempre será Internacao
	
						cs.execute();
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			};
			
			try {
				LOG.debug("Executando por callableStatement " +  ObjetosBancoOracleEnum.AIPK_TROCA_CONVENIO_AGHU_EVT_ON_INSERT.toString());				
				super.doWork(work);					
			
			} catch (final Exception e) {
				throwExceptionStoredProcedure(storedProcedureName, e, internacaoSeq, cnvCodigo, seqConv, dtIntAdminNovo, cthSeqAtual, dtIntAdminAtual, dtAltaAdminAtual, cnvCodigoOld);
			}
			if (work.getException() != null){
				throwExceptionStoredProcedure(storedProcedureName, work.getException(), internacaoSeq, cnvCodigo, seqConv, dtIntAdminNovo, cthSeqAtual, dtIntAdminAtual, dtAltaAdminAtual, cnvCodigoOld);
			}
		}

		/**
		 * @ORADB AIPP_SUBS_PRONT_ATD
		 * 
		 * @param codigoPacienteOrigem
		 * @param codigoPacienteDestino
		 * @param atdSeq
		 * @param servidorLogado
		 * @throws ApplicationBusinessException
		 */
		public void executaSubstituirProntuarioAtendimento(final Integer codigoPacienteOrigem, final Integer codigoPacienteDestino, final Integer atdSeq,
				final RapServidores servidorLogado) throws ApplicationBusinessException {
			
			if (isOracle()) {

				final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AIPP_SUBS_PRONT_ATD.toString();
				
				AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {

							@Override
							public void executeAghProcedure(final Connection connection) throws SQLException {

								CallableStatement statement = null;
								try {
									final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?)}");

									statement = connection.prepareCall(call	.toString());

									CoreUtil.configurarParametroCallableStatement(statement, 1, Types.INTEGER, codigoPacienteOrigem); //P_CODIGO_ORIGEM IN NUMBER
									CoreUtil.configurarParametroCallableStatement(statement, 2, Types.INTEGER, codigoPacienteDestino); //P_CODIGO_DESTINO IN NUMBER
									CoreUtil.configurarParametroCallableStatement(statement, 3, Types.INTEGER, atdSeq); //P_atd_SEQ IN NUMBER

									statement.execute();
								} finally {
									if (statement != null) {
										statement.close();
									}
								}

							}
						};
				try {
					super.doWork(work);
					LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.AIPP_SUBS_PRONT_ATD.toString());
				} catch (final Exception e) {
					throwExceptionStoredProcedure(storedProcedureName, e, codigoPacienteOrigem, codigoPacienteDestino, atdSeq);
				}
				if (work.getException() != null){
					throwExceptionStoredProcedure(storedProcedureName, work.getException(), codigoPacienteOrigem, codigoPacienteDestino, atdSeq);
				}
			}
		}

		public void executaProceduresRNFatpExecFatNew(final ObjetosBancoOracleEnum enumPrc, final java.util.Date dtCompetencia, final Boolean pPrevia, final RapServidores servidorLogado) throws ApplicationBusinessException {
			
			final String previa = (pPrevia ? "S" : "N");
			
			if (isOracle()) {

				final String storedProcedureName = EsquemasOracleEnum.AGH + "." + enumPrc.toString();
				
				AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {

							@Override
							public void executeAghProcedure(final Connection connection) throws SQLException {

								CallableStatement statement = null;
								try {
									if(ObjetosBancoOracleEnum.FATK_PMR_RN_RN_PMRP_ATU_APAC_FUT.equals(enumPrc)){
										final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?)}");
										statement = connection.prepareCall(call.toString());
										CoreUtil.configurarParametroCallableStatement(statement, 1, Types.VARCHAR, previa); // P_PREVIA IN VARCHAR2
										
									} else {
										
										final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?)}");
										
										statement = connection.prepareCall(call.toString());
										
										CoreUtil.configurarParametroCallableStatement(statement, 1, Types.VARCHAR, previa); // P_PREVIA IN VARCHAR2
										CoreUtil.configurarParametroCallableStatement(statement, 2, Types.TIMESTAMP, dtCompetencia); 	// p_cpe_dt_fim DATE
									}

									statement.execute();
								} finally {
									if (statement != null) {
										statement.close();
									}
								}

							}
						};
				try {
					super.doWork(work);
					LOG.info("Executou por chamada nativa " + enumPrc.toString());
				} catch (final Exception e) {
					throwExceptionStoredProcedure(storedProcedureName, e, previa, dtCompetencia);
				}
				if (work.getException() != null){
					throwExceptionStoredProcedure(storedProcedureName, work.getException(), previa, dtCompetencia);
				}
			}
		}		
	
		
		public void gerarRmProcedimento(final Integer atdSeq, final Short pedSeq, final String observacao, String usuarioLogado)
				throws ApplicationBusinessException 
				{
					if (!this.isOracle()) {
						LOG.debug("Esta flag só será setada quando o banco em uso for Oracle. Ignorando commando...");
						return;
					}
					
					final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.SCEK_RMS_RN_RN_RMSP_GERA_RM_PRCS.toString();
					
					AghWork work = new AghWork(usuarioLogado) {
						public void executeAghProcedure(Connection connection) throws SQLException {
							
							CallableStatement cs = null;
							try {
								StringBuilder sbCall = new StringBuilder("{call ");
								sbCall.append(nomeObjeto);
								sbCall.append("(?,?,?,?,?)}");
				
								cs = connection.prepareCall(sbCall.toString());
				
								CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, atdSeq);
								CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, pedSeq);
								CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER, 0);
								CoreUtil.configurarParametroCallableStatement(cs, 4, Types.INTEGER, 1);
								CoreUtil.configurarParametroCallableStatement(cs, 5, Types.VARCHAR, observacao);
				
								cs.execute();
							} finally {
								if (cs != null) {
									cs.close();
								}
							}
						}
					};
					
					try {
						super.doWork(work);
						
					} catch (Exception e) {
						// Tratar erro com mensagem padrão para problemas genéricos em
						// execução de procedures/functions do Oracle
						String valores = CoreUtil.configurarValoresParametrosCallableStatement(atdSeq, pedSeq, 0, 1 , observacao);
						LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
						throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
								CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
					}
					
					if (work.getException() != null) {
						// Tratar erro com mensagem padrão para problemas genéricos em
						// execução de procedures/functions do Oracle
						String valores = CoreUtil
								.configurarValoresParametrosCallableStatement(atdSeq,
										pedSeq, 0, 1, observacao);
						LOG.error(CoreUtil
								.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
										work.getException(), true, valores), work.getException());
						throw new ApplicationBusinessException(
								AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
								nomeObjeto, valores, CoreUtil
										.configurarMensagemUsuarioLogCallableStatement(
												nomeObjeto, work.getException(), false,
												valores));
					}
					
				}		

		
		/**
		 * @ORADB mpmp_gera_mvto_ece
		 **/
		public void geraMvtoEce(final Integer atdSeq, final Integer seq, final java.util.Date dtIncioMvtoPendente, final java.util.Date dtMvto, final java.util.Date dtIncio, final java.util.Date dtFim, final java.util.Date dtReferencia, final String usuarioLogado)
		throws ApplicationBusinessException 
		{
			if (!this.isOracle()) {
				LOG.debug("Esta flag só será setada quando o banco em uso for Oracle. Ignorando commando...");
				return;
			}
			
			final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.MPMP_GERA_MVTO_ECE.toString();
			
			final java.sql.Timestamp dtMvtoTm = new java.sql.Timestamp(dtMvto.getTime());
			
			AghWork work = new AghWork(usuarioLogado) {
				public void executeAghProcedure(Connection connection) throws SQLException {
					
					CallableStatement cs = null;
					try {
						StringBuilder sbCall = new StringBuilder("{call ");
						sbCall.append(nomeObjeto);
						sbCall.append("(?,?,?,?,?,?,?)}");
		
						cs = connection.prepareCall(sbCall.toString());
		
						CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, atdSeq);
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, seq);
						CoreUtil.configurarParametroCallableStatement(cs, 3, Types.TIMESTAMP, dtIncioMvtoPendente);
						CoreUtil.configurarParametroCallableStatement(cs, 4, Types.TIMESTAMP, dtMvtoTm);
						CoreUtil.configurarParametroCallableStatement(cs, 5, Types.TIMESTAMP, dtIncio);
						CoreUtil.configurarParametroCallableStatement(cs, 6, Types.TIMESTAMP, dtFim);
						CoreUtil.configurarParametroCallableStatement(cs, 7, Types.TIMESTAMP, dtReferencia);
		
						cs.execute();
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			};
			
			try {
				super.doWork(work);
				
			} catch (Exception e) {
				// Tratar erro com mensagem padrão para problemas genéricos em
				// execução de procedures/functions do Oracle
				String valores = CoreUtil.configurarValoresParametrosCallableStatement(atdSeq, seq, dtIncioMvtoPendente, dtIncio, dtFim, dtReferencia);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
			}
			
			if (work.getException() != null){
				// Tratar erro com mensagem padrão para problemas genéricos em
				// execução de procedures/functions do Oracle
				String valores = CoreUtil.configurarValoresParametrosCallableStatement(atdSeq, seq, dtIncioMvtoPendente, dtMvtoTm, dtIncio, dtFim, dtReferencia);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), true, valores), work.getException());
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), false, valores));
			}
			
		}

		/**
		 * @ORADB mpmp_inclui_prcr_ece
		 **/
		public void incluiPrcrEce(final Integer atdSeq, final Integer seq, final java.util.Date dtIncio, final java.util.Date dtFim, final java.util.Date dtReferencia, final String usuarioLogado)
		throws ApplicationBusinessException 
		{
			if (!this.isOracle()) {
				LOG.debug("Esta flag só será setada quando o banco em uso for Oracle. Ignorando commando...");
				return;
			}
			
			final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.MPMP_INCLUI_PRCR_ECE.toString();
			
			AghWork work = new AghWork(usuarioLogado) {
				public void executeAghProcedure(Connection connection) throws SQLException {
					
					CallableStatement cs = null;
					try {
						StringBuilder sbCall = new StringBuilder("{call ");
						sbCall.append(nomeObjeto);
						sbCall.append("(?,?,?,?,?)}");
		
						cs = connection.prepareCall(sbCall.toString());
		
						CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, atdSeq);
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, seq);
						CoreUtil.configurarParametroCallableStatement(cs, 3, Types.TIMESTAMP, dtIncio);
						CoreUtil.configurarParametroCallableStatement(cs, 4, Types.TIMESTAMP, dtFim);
						CoreUtil.configurarParametroCallableStatement(cs, 5, Types.TIMESTAMP, dtReferencia);
		
						cs.execute();
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			};
			
			try {
				super.doWork(work);
				
			} catch (Exception e) {
				// Tratar erro com mensagem padrão para problemas genéricos em
				// execução de procedures/functions do Oracle
				String valores = CoreUtil.configurarValoresParametrosCallableStatement(atdSeq, seq, dtIncio, dtFim, dtReferencia);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
			}
		
			if (work.getException() != null) {
				// Tratar erro com mensagem padrão para problemas genéricos em
				// execução de procedures/functions do Oracle
				String valores = CoreUtil
						.configurarValoresParametrosCallableStatement(atdSeq, seq,
								dtIncio, dtFim, dtReferencia);
				LOG.error(CoreUtil
						.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
								work.getException(), true, valores), work.getException());
				throw new ApplicationBusinessException(
						AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
						nomeObjeto, valores, CoreUtil
								.configurarMensagemUsuarioLogCallableStatement(
										nomeObjeto, work.getException(), false, valores));
			}
			
		}

		/**
		 * @ORADB mpmp_gera_ordem_adm
		 **/
		public void geraOrdemAdm(final Integer atdSeq, final Integer seq, final java.util.Date dtIncio, final java.util.Date dtFim, final java.util.Date dtReferencia, final String usuarioLogado)
		throws ApplicationBusinessException 
		{
			if (!this.isOracle()) {
				LOG.debug("Esta flag só será setada quando o banco em uso for Oracle. Ignorando commando...");
				return;
			}
			
			final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.MPMP_GERA_ORDEM_ADM.toString();
			AghWork work = new AghWork(usuarioLogado) {
				public void executeAghProcedure(Connection connection) throws SQLException {
					
					CallableStatement cs = null;
					try {
						StringBuilder sbCall = new StringBuilder("{call ");
						sbCall.append(nomeObjeto);
						sbCall.append("(?,?,?,?,?)}");
		
						cs = connection.prepareCall(sbCall.toString());
		
						CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, atdSeq);
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, seq);
						CoreUtil.configurarParametroCallableStatement(cs, 3, Types.TIMESTAMP, dtIncio);
						CoreUtil.configurarParametroCallableStatement(cs, 4, Types.TIMESTAMP, dtFim);
						CoreUtil.configurarParametroCallableStatement(cs, 5, Types.TIMESTAMP, dtReferencia);
		
						cs.execute();
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			};
			
			try {
				super.doWork(work);
				
			} catch (Exception e) {
				// Tratar erro com mensagem padrão para problemas genéricos em
				// execução de procedures/functions do Oracle
				String valores = CoreUtil.configurarValoresParametrosCallableStatement(atdSeq, seq, dtIncio, dtFim, dtReferencia);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
			}
			
			if (work.getException() != null) {
				// Tratar erro com mensagem padrão para problemas genéricos em
				// execução de procedures/functions do Oracle
				String valores = CoreUtil
						.configurarValoresParametrosCallableStatement(atdSeq, seq,
								dtIncio, dtFim, dtReferencia);
				LOG.error(CoreUtil
						.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
								work.getException(), true, valores), work.getException());
				throw new ApplicationBusinessException(
						AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
						nomeObjeto, valores, CoreUtil
								.configurarMensagemUsuarioLogCallableStatement(
										nomeObjeto, work.getException(), false, valores));
			}
			
		}
		
		
		/**
		 * @ORADB mpmp_gera_pista_mci
		 **/
		public void geraPistaMci(final Integer atdSeq, final Integer seq, final java.util.Date dtIncio, final java.util.Date dtFim, final String usuarioLogado)
		throws ApplicationBusinessException 
		{
			if (!this.isOracle()) {
				LOG.debug("Esta flag só será setada quando o banco em uso for Oracle. Ignorando commando...");
				return;
			}
			
			final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.MPMP_GERA_PISTA_MCI.toString();
			AghWork work = new AghWork(usuarioLogado) {
				public void executeAghProcedure(Connection connection) throws SQLException {
					
					CallableStatement cs = null;
					try {
						StringBuilder sbCall = new StringBuilder("{call ");
						sbCall.append(nomeObjeto);
						sbCall.append("(?,?,?,?)}");
		
						cs = connection.prepareCall(sbCall.toString());
		
						CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, atdSeq);
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, seq);
						CoreUtil.configurarParametroCallableStatement(cs, 3, Types.TIMESTAMP, dtIncio);
						CoreUtil.configurarParametroCallableStatement(cs, 4, Types.TIMESTAMP, dtFim);
		
						cs.execute();
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			};
			
			try {
				super.doWork(work);
				
			} catch (Exception e) {
				// Tratar erro com mensagem padrão para problemas genéricos em
				// execução de procedures/functions do Oracle
				String valores = CoreUtil.configurarValoresParametrosCallableStatement(atdSeq, seq, dtIncio, dtFim);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
			}
			
			if (work.getException() != null) {
				// Tratar erro com mensagem padrão para problemas genéricos em
				// execução de procedures/functions do Oracle
				String valores = CoreUtil
						.configurarValoresParametrosCallableStatement(atdSeq, seq,
								dtIncio, dtFim);
				LOG.error(CoreUtil
						.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
								work.getException(), true, valores), work.getException());
				throw new ApplicationBusinessException(
						AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
						nomeObjeto, valores, CoreUtil
								.configurarMensagemUsuarioLogCallableStatement(
										nomeObjeto, work.getException(), false, valores));
			}
			
		}		
	
		/**
		 * @ORADB FFC_GERA_CONTA_INT
		 */
		public void geraContaInternacaoFaturamentoConvenios(final Integer intSeq, final Integer cthSeq, final RapServidores servidorLogado) throws ApplicationBusinessException {
			if (isOracle()) {
				final String storedProcedureName = ObjetosBancoOracleEnum.FFC_GERA_CONTA_INT.toString();
				
				AghWork work = new AghWork(servidorLogado != null ? servidorLogado.getUsuario() : null) {

							@Override
							public void executeAghProcedure(final Connection connection) throws SQLException {

								CallableStatement statement = null;
								try {
									final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?)}");

									statement = connection.prepareCall(call	.toString());

									CoreUtil.configurarParametroCallableStatement(statement, 1, Types.INTEGER, intSeq); //P_INT_SEQ IN NUMBER
									CoreUtil.configurarParametroCallableStatement(statement, 2, Types.INTEGER, cthSeq); //P_CTH_SEQ IN NUMBER

									statement.execute();
								} finally {
									if (statement != null) {
										statement.close();
									}
								}

							}
						};
				try {
					super.doWork(work);
					LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.FFC_GERA_CONTA_INT.toString());
				} catch (final Exception e) {
					throwExceptionStoredProcedure(storedProcedureName, e, intSeq, cthSeq);
				}
				if (work.getException() != null){
					throwExceptionStoredProcedure(storedProcedureName, work.getException(), intSeq, cthSeq);
				}
			}
		}

		/**
		 * @ORADB chamada nativa EPEP_GERA_PISTA_MCI
		 * 
		 *        insere na tabela MCI_MVTO_PROCEDIMENTO_RISCOS
		 * 
		 * @param atdSeq
		 * @param seq (EpePrescEnfermagem)
		 * @param servidorLogado
		 * @throws AGHUNegocioException
		 */
		
		public void inserirMvtoProcedimentoRiscos(final Integer atdSeq, final Integer seq, 
				final java.util.Date dthrInicio, final java.util.Date dthrFim, 
				final String usuarioLogado) throws ApplicationBusinessException {
			if (isOracle()) {
				final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.EPEP_GERA_PISTA_MCI.toString();
				
				AghWork work = new AghWork(usuarioLogado) {
					
					@Override
					public void executeAghProcedure(Connection connection) throws SQLException {

						CallableStatement statement = null;					
						try {
							final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?)}");

							statement = connection.prepareCall(call.toString());

							CoreUtil.configurarParametroCallableStatement(statement, 1, Types.INTEGER, atdSeq);
							CoreUtil.configurarParametroCallableStatement(statement, 2, Types.INTEGER, seq);
							CoreUtil.configurarParametroCallableStatement(statement, 3, Types.TIMESTAMP, dthrInicio); 
							CoreUtil.configurarParametroCallableStatement(statement, 4, Types.TIMESTAMP, dthrFim); 
													
							statement.execute();
						} finally {
							if (statement != null) {
								statement.close();
							}
						}
					}
				};
				
				try {
					super.doWork(work);
					LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.EPEP_GERA_PISTA_MCI.toString());
				} catch (final Exception e) {
					throwExceptionStoredProcedure(storedProcedureName, e, atdSeq, seq, dthrInicio, dthrFim);
				}
				if (work.getException() != null){
					throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq, seq, dthrInicio, dthrFim);
				}
			}
			
		}
		
		
		/**
		 * @ORADB chamada nativa EPEP_GERA_ORDEM_ECE
		 * 
		 *        insere na tabela ECE_ORDEM_DE_ADMINISTRACOES, ECE_ORDEM_X_LOCALIZACAO, 
		 * 
		 * @param atdSeq
		 * @param seq (EpePrescEnfermagem)
		 * @param dthr_inicio, dthr_fim
		 * @param servidorLogado
		 * @throws AGHUNegocioException
		 */
			
		public void inserirEceOrdemLocalizacao(final Integer atdSeq, final Integer seq, 
				final java.util.Date dthrInicio, final java.util.Date dthrFim, final java.util.Date dtReferencia,
				final String usuarioLogado) throws ApplicationBusinessException {
			if (isOracle()) {
				final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.EPEP_GERA_ORDEM_ECE.toString();
				
				AghWork work = new AghWork(usuarioLogado) {
					
					@Override
					public void executeAghProcedure(Connection connection) throws SQLException {

						CallableStatement statement = null;					
						try {
							final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?,?)}");

							statement = connection.prepareCall(call.toString());

							statement.setInt(1, atdSeq);
							statement.setInt(2, seq);
							CoreUtil.configurarParametroCallableStatement(statement, 3, Types.TIMESTAMP, dthrInicio); 
							CoreUtil.configurarParametroCallableStatement(statement, 4, Types.TIMESTAMP, dthrFim); 
							CoreUtil.configurarParametroCallableStatement(statement, 5, Types.TIMESTAMP, dtReferencia);
							
							statement.execute();
						} finally {
							if (statement != null) {
								statement.close();
							}
						}
					}
				};
				
				try {
					super.doWork(work);
					LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.EPEP_GERA_ORDEM_ECE.toString());
				} catch (final Exception e) {
					throwExceptionStoredProcedure(storedProcedureName, e, atdSeq, seq, dthrInicio, dthrFim);
				}
				if (work.getException() != null){
					throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq, seq, dthrInicio, dthrFim);
				}
			}
			
		}
		

		/**
		 * @ORADB chamada nativa EPEP_GERA_MVCO_ECE
		 * 
		 *   update ECE_ITEM_ADMINISTRADOS, ECE_HORARIO_ADMINISTRADOS, ECE_TURNO_ITEM_ADMINISTRADOS, ECE_ITEM_ADMINISTRADOS
		 *   Chama EPEP_INCLUI_ITEM_ECE
		 * 
		 * @param atdSeq
		 * @param seq (EpePrescEnfermagem)
		 * @param dthr_inicio_mvto_pendente
		 * @param dthr_movimento
		 * @param dthr_inicio, dthr_fim, dt_referencia
		 * @param servidorLogado
		 * @throws AGHUNegocioException
		 */	
		
		public void gerarMvtoEnfermagemChecagemEletronica(final Integer atdSeq, final Integer seq, 
				final java.util.Date dthIniMvtoPendente, final java.util.Date dthMovimento, 
				final java.util.Date dthInicio, final java.util.Date dthFim, final java.util.Date dtReferencia,
				final String usuarioLogado) throws ApplicationBusinessException {
			if (isOracle()) {
				final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.EPEP_GERA_MVTO_ECE.toString();
				
				AghWork work = new AghWork(usuarioLogado) {
					
					@Override
					public void executeAghProcedure(Connection connection) throws SQLException {

						CallableStatement statement = null;					
						try {
							final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?,?,?,?)}");

							statement = connection.prepareCall(call.toString());

							statement.setInt(1, atdSeq);
							statement.setInt(2, seq);
							CoreUtil.configurarParametroCallableStatement(statement, 3, Types.TIMESTAMP, dthIniMvtoPendente); 
							CoreUtil.configurarParametroCallableStatement(statement, 4, Types.TIMESTAMP, dthMovimento); 
							CoreUtil.configurarParametroCallableStatement(statement, 5, Types.TIMESTAMP, dthInicio); 
							CoreUtil.configurarParametroCallableStatement(statement, 6, Types.TIMESTAMP, dthFim); 
							CoreUtil.configurarParametroCallableStatement(statement, 7, Types.DATE, dtReferencia); 						
							statement.execute();
						} finally {
							if (statement != null) {
								statement.close();
							}
						}
					}
				};
				
				try {
					super.doWork(work);
					LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.EPEP_GERA_MVTO_ECE.toString());
				} catch (final Exception e) {
					throwExceptionStoredProcedure(storedProcedureName, e, atdSeq, seq, 
							 					dthIniMvtoPendente,  dthMovimento, dthInicio, dthFim);
				}
				if (work.getException() != null){
					throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq, seq, dthInicio, dthFim);
				}
			}
			
		}
		
		
		/**
		 * @ORADB chamada nativa EPEP_GERA_ITEM_ECE
		 * 
		 *        insere na tabela ECE_ORDEM_DE_ADMINISTRACOES, ECE_ORDEM_X_LOCALIZACAO, 
		 * 
		 * @param atdSeq
		 * @param seq (EpePrescEnfermagem)
		 * @param dthr_inicio, dthr_fim
		 * @param servidorLogado
		 * @throws AGHUNegocioException
		 */
			
		public void inserirEceItemAdministrados(final Integer atdSeq, final Integer seq, 
				final java.util.Date dthrInicio, final java.util.Date dthrFim, final java.util.Date dtReferencia,
				final String usuarioLogado) throws ApplicationBusinessException {
			if (isOracle()) {
				final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.EPEP_GERA_ITEM_ECE.toString();
				
				AghWork work = new AghWork(usuarioLogado) {
					
					@Override
					public void executeAghProcedure(Connection connection) throws SQLException {

						CallableStatement statement = null;					
						try {
							final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?,?)}");

							statement = connection.prepareCall(call.toString());

							statement.setInt(1, atdSeq);
							statement.setInt(2, seq);
							CoreUtil.configurarParametroCallableStatement(statement, 3, Types.TIMESTAMP, dthrInicio); 
							CoreUtil.configurarParametroCallableStatement(statement, 4, Types.TIMESTAMP, dthrFim); 
							CoreUtil.configurarParametroCallableStatement(statement, 5, Types.DATE, dtReferencia); 
							
							statement.execute();
						} finally {
							if (statement != null) {
								statement.close();
							}
						}
					}
				};
				
				try {
					super.doWork(work);
					LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.EPEP_GERA_ITEM_ECE.toString());
				} catch (final Exception e) {
					throwExceptionStoredProcedure(storedProcedureName, e, atdSeq, seq, dthrInicio, dthrFim, dtReferencia);
				}
				if (work.getException() != null){
					throwExceptionStoredProcedure(storedProcedureName, work.getException(), atdSeq, seq, dthrInicio, dthrFim, dtReferencia);
				}
			}
		}
		
		
		/**
		 * #42229 Incluido
		 * @ORADB chamada nativa FFC_INTERFACE_AAC_PRJ
		 * @param p_data
		 * @param p_numero
		 * @param oldSitCodigo
		 * @param newSitCodigo
		 * @param matricula
		 * @param vinCodigo
		 * @param usuarioLogado
		 * @throws AGHUNegocioException
		 */
			
		public void ffcInterfaceAACPRJ(final String pData, final Integer pNumero, 
				final Short oldSitCodigo, final Short newSitCodigo, final Integer matricula,
				final Integer vinCodigo, final String usuarioLogado) throws ApplicationBusinessException {
			if (isOracle() && isHCPA()) {
				final String storedProcedureName = EsquemasOracleEnum.CONV + "." + ObjetosBancoOracleEnum.FFC_INTERFACE_AAC_PRJ.toString();
				AghWork work = new AghWork(usuarioLogado) {
					@Override
					public void executeAghProcedure(Connection connection) throws SQLException {
						CallableStatement statement = null;					
						try {
							final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,?,?,?,?,?)}");
							statement = connection.prepareCall(call.toString());
							statement.setString(1, pData);
							statement.setInt(2, pNumero);
							statement.setShort(3, oldSitCodigo);
							statement.setShort(4, newSitCodigo);
							statement.setInt(5,matricula);
							statement.setInt(6, vinCodigo);
							statement.execute();
						} finally {
							if (statement != null) {
								statement.close();
							}
						}
					}
				};
				try {
					super.doWork(work);
					LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.FFC_INTERFACE_AAC_PRJ.toString());
				} catch (final Exception e) {
					throwExceptionStoredProcedure(storedProcedureName, e, pData, pNumero, oldSitCodigo, newSitCodigo, matricula,vinCodigo);
				}
				if (work.getException() != null){
					throwExceptionStoredProcedure(storedProcedureName, work.getException(), pData, pNumero, oldSitCodigo, newSitCodigo, matricula,vinCodigo);
				}
			}
		}
		
		public void executarRotinaSIAF(final Integer afNumero, final String usuarioLogado) throws ApplicationBusinessException {
			if (isOracle()) {
				final String storedProcedureName = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.RN_IAFP_GER_LI_SIAFI.toString();
				
				AghWork work = new AghWork(usuarioLogado) {
					
					@Override
					public void executeAghProcedure(Connection connection) throws SQLException {

						CallableStatement statement = null;					
						try {
							final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?,null,2)}");

							statement = connection.prepareCall(call.toString());

							CoreUtil.configurarParametroCallableStatement(statement, 1, Types.INTEGER, afNumero);
													
							statement.execute();
						} finally {
							if (statement != null) {
								statement.close();
							}
						}
					}
				};
				
				try {
					super.doWork(work);
					LOG.info("Executou por chamada nativa " + ObjetosBancoOracleEnum.RN_IAFP_GER_LI_SIAFI.toString());
				} catch (final Exception e) {
					throwExceptionStoredProcedure(storedProcedureName, e, afNumero);
				}
				if (work.getException() != null){
					throwExceptionStoredProcedure(storedProcedureName, work.getException(), afNumero);
				}
			}
			
		}
}
