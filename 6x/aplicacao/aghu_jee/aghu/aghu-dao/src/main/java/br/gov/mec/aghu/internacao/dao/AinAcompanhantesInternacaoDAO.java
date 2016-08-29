package br.gov.mec.aghu.internacao.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.internacao.vo.ValidaContaTrocaConvenioVO;
import br.gov.mec.aghu.model.AinAcompanhantesInternacao;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class AinAcompanhantesInternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinAcompanhantesInternacao> {
	
	
	private static final long serialVersionUID = -62094786111570168L;
	
	private static final Log LOG = LogFactory.getLog(AinAcompanhantesInternacaoDAO.class);	
	
	private enum AinAcompanhantesInternacaoDAOxceptionCode implements BusinessExceptionCode {
		AIN_00894, CONTA_ENCERRADA
	}

	/**
	 * Retorna os acompanhantes na internação.
	 * 
	 * @param seq
	 *            da Internação.
	 * @return Lista de Acomponhantes
	 */
	public List<AinAcompanhantesInternacao> obterAcompanhantesInternacao(Integer seq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinAcompanhantesInternacao.class);

		criteria.add(Restrictions.eq(AinAcompanhantesInternacao.Fields.INT_SEQ.toString(), seq));

		return executeCriteria(criteria);

	}

	public List<AinAcompanhantesInternacao> obterAcompanhantesInternacao(List<Integer> intSeqs) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinAcompanhantesInternacao.class);
		criteria.add(Restrictions.in(AinAcompanhantesInternacao.Fields.INT_SEQ.toString(), intSeqs));
		criteria.addOrder(Order.asc(AinAcompanhantesInternacao.Fields.INT_SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna a criteria de recuperação de
	 * <code>AinAcompanhantesInternacao</code>.
	 * 
	 * @param codigo
	 *            do
	 * @return o DetachedCriteria para ser usado em outros métodos
	 */
	private DetachedCriteria criarCriteriaAinAcompanhantesInternacao(AinInternacao internacao) {
		if (internacao == null || internacao.getSeq() == null) {
			// Nunca deveria ser nulo.
			throw new IllegalArgumentException("Internação não informada.");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(AinAcompanhantesInternacao.class);
		criteria.add(Restrictions.eq(AinAcompanhantesInternacao.Fields.INT_SEQ.toString(), internacao.getSeq()));
		return criteria;
	}

	public List<AinAcompanhantesInternacao> pesquisarAinAcompanhantesInternacao(AinInternacao internacao) {
		DetachedCriteria criteria = this.criarCriteriaAinAcompanhantesInternacao(internacao);
		return executeCriteria(criteria);
	}
	
	/**
	 * Obtem sequence da tabela
	 * 
	 * @param acompanhanteInternacao
	 */
	public Byte obterValorSeqId(AinAcompanhantesInternacao acompanhanteInternacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinAcompanhantesInternacao.class);

		if (acompanhanteInternacao.getId() != null && acompanhanteInternacao.getId().getIntSeq() != null) {
			criteria.add(Restrictions.eq(AinAcompanhantesInternacao.Fields.INT_SEQ.toString(), acompanhanteInternacao.getId()
					.getIntSeq()));
			criteria.setProjection(Projections.max(AinAcompanhantesInternacao.Fields.SEQ.toString()));
		} else {
			return (byte) 1;
		}

		Byte obj = (Byte) executeCriteriaUniqueResult(criteria);
		if (obj == null) {
			return (byte) 1;
		}
		return (byte) (obj.intValue() + 1);
	}
	
	public int verificarNumeroAcompanhantes(Integer intSeq) {
		int retorno = 0;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinAcompanhantesInternacao.class);
		criteria.add(Restrictions.eq(AinAcompanhantesInternacao.Fields.INT_SEQ
				.toString(), intSeq));

		List<AinAcompanhantesInternacao> listaAcompanhantes = this
				.executeCriteria(criteria);

		if (listaAcompanhantes.size() > 0) {
			retorno = listaAcompanhantes.size();
		}

		return retorno;
	}
	
	public void gerarCheckoutAtu(final Short seqUnidadeFuncional,
			final Short numeroQuarto, final String leitoID,
			final Short seqUnidadeFuncionalAntigo,
			final Short numeroQuartoAntigo, final String leitoIDAntigo,
			final Integer seqAtendimentoUrgencia, final Integer codigoPaciente,
			final String codigoTipoAltaMedicaAntigo,
			final String codigoTipoAltaMedica,
			final boolean indPacienteEmAtendimentoAntigo,
			final boolean indPacienteEmAtendimento, final String usuarioLogado,
			final Short codTipoAltaMedicaAntigo, 
			final Short codTipoAltaMedica ) throws ApplicationBusinessException{

		
		final String nomeObjeto = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.MAMP_CHECK_OUT_ATU;		
		
		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(final Connection connection) throws SQLException {
				
				CallableStatement cs = null;
				try {
					final StringBuilder sbCall = new StringBuilder(50).append("{call ");
					sbCall.append(nomeObjeto);
					sbCall.append("(?,?,?,?,?,?,?,?)}");

					cs = connection.prepareCall(sbCall.toString());

					int i = 1;
					CoreUtil.configurarParametroCallableStatement(cs, i++,
							Types.INTEGER, seqAtendimentoUrgencia);
					CoreUtil.configurarParametroCallableStatement(cs, i++,
							Types.INTEGER, codigoPaciente);
					CoreUtil.configurarParametroCallableStatement(cs, i++,
							Types.VARCHAR, codigoTipoAltaMedicaAntigo);
					CoreUtil.configurarParametroCallableStatement(cs, i++,
							Types.VARCHAR, codigoTipoAltaMedica);
					CoreUtil.configurarParametroCallableStatement(cs, i++,
							Types.INTEGER, codTipoAltaMedicaAntigo);
					CoreUtil.configurarParametroCallableStatement(cs, i++,
							Types.INTEGER, codTipoAltaMedica);

					// CoreUtil não trata tipos BOOLEAN
					cs.setBoolean(i++, indPacienteEmAtendimentoAntigo);
					cs.setBoolean(i++, indPacienteEmAtendimento);

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
					.configurarValoresParametrosCallableStatement(
							seqUnidadeFuncional, numeroQuarto, leitoID,
							seqUnidadeFuncionalAntigo, numeroQuartoAntigo,
							leitoIDAntigo, seqAtendimentoUrgencia,
							codigoPaciente, codTipoAltaMedicaAntigo,
							codTipoAltaMedica, indPacienteEmAtendimentoAntigo,
							indPacienteEmAtendimento);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							e, true, valores), e);
			throw new ApplicationBusinessException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, e, false, valores));
		}
		
		if (work.getException() != null){
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(
							seqUnidadeFuncional, numeroQuarto, leitoID,
							seqUnidadeFuncionalAntigo, numeroQuartoAntigo,
							leitoIDAntigo, seqAtendimentoUrgencia,
							codigoPaciente, codTipoAltaMedicaAntigo,
							codTipoAltaMedica, indPacienteEmAtendimentoAntigo,
							indPacienteEmAtendimento);
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
	
	
	public void verificarBloqueioNeurologia(final String leitoId,
			final Short numeroQuarto, final Short seqUnidadeFuncional,
			final Short seqEspecialidade, final String usuarioLogado)
			throws ApplicationBusinessException {
		final String nomeObjeto = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.RN_INTP_BLOQ_NEURO;

		AghWork work = new AghWork(usuarioLogado) {
			@Override
			public void executeAghProcedure(final Connection connection)
					throws SQLException {

				CallableStatement cs = null;
				try {
					final StringBuilder sbCall = new StringBuilder("{call ");
					sbCall.append(nomeObjeto);
					sbCall.append("(?,?,?,?)}");

					cs = connection.prepareCall(sbCall.toString());

					CoreUtil.configurarParametroCallableStatement(cs, 1,
							Types.VARCHAR, leitoId);
					CoreUtil.configurarParametroCallableStatement(cs, 2,
							Types.INTEGER, numeroQuarto);
					CoreUtil.configurarParametroCallableStatement(cs, 3,
							Types.INTEGER, seqUnidadeFuncional);
					CoreUtil.configurarParametroCallableStatement(cs, 4,
							Types.INTEGER, seqEspecialidade);

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
			LOG.error(e.getMessage(), e);
			if (e.getCause().getMessage().indexOf("AIN_00894") > -1) {
				throw new ApplicationBusinessException(
						AinAcompanhantesInternacaoDAOxceptionCode.AIN_00894);
			} else {
				final String valores = CoreUtil
						.configurarValoresParametrosCallableStatement(leitoId,
								numeroQuarto, seqUnidadeFuncional,
								seqEspecialidade);
				LOG.error(CoreUtil
						.configurarMensagemUsuarioLogCallableStatement(
								nomeObjeto, e, true, valores), e);
				throw new ApplicationBusinessException(
						AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
						nomeObjeto, valores, CoreUtil
								.configurarMensagemUsuarioLogCallableStatement(
										nomeObjeto, e, false, valores));
			}
		}

		if (work.getException() != null) {
			LOG.error(work.getException().getMessage(), work.getException());
			if (work.getException().getMessage().indexOf("AIN_00894") > -1) {
				throw new ApplicationBusinessException(
						AinAcompanhantesInternacaoDAOxceptionCode.AIN_00894);
			} else {
				final String valores = CoreUtil
						.configurarValoresParametrosCallableStatement(leitoId,
								numeroQuarto, seqUnidadeFuncional,
								seqEspecialidade);
				LOG.error(CoreUtil
						.configurarMensagemUsuarioLogCallableStatement(
								nomeObjeto, work.getException(), true, valores), work.getException());
				throw new ApplicationBusinessException(
						AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
						nomeObjeto, valores, CoreUtil
								.configurarMensagemUsuarioLogCallableStatement(
										nomeObjeto, work.getException(), false,
										valores));
			}
		}
	}
	
	
	
	public Double verificarValorAcomodacao(final Integer codAcom,
			final Date dthrInternacao, final String usuarioLogado) throws ApplicationBusinessException {
		
		class ValrAcomVO{
			private Double valAcom;
			
			public Double getValAcom() {
				return this.valAcom;
			}
			public void setValAcom(final Double valAcom) {
				this.valAcom = valAcom;
			}
		}

		final ValrAcomVO vo = new ValrAcomVO();
		final String nomeObjeto = ObjetosBancoOracleEnum.FFC_F_VALR_ACOM
				.toString();
		
		AghWork work = new AghWork(usuarioLogado) {
		    @Override
			public void executeAghProcedure(final Connection connection) throws SQLException {
		    	
		    	CallableStatement cs = null;
		    	try{
					cs = connection.prepareCall("{? = call " + nomeObjeto
							+ "(?,?)}");
		    		
		    		// Bind dos parametros de entrada
					CoreUtil.configurarParametroCallableStatement(cs, 2,
							Types.INTEGER, codAcom);
					CoreUtil.configurarParametroCallableStatement(cs, 3,
							Types.TIMESTAMP, dthrInternacao);
		    		
		    		// Bind do parametro de saída - OUT (retorno da function)
		    		cs.registerOutParameter(1, Types.DOUBLE);
		    		
		    		cs.execute();
		    		vo.setValAcom(cs.getDouble(1));
		    	}finally {
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
					.configurarValoresParametrosCallableStatement(codAcom,
							dthrInternacao);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							e, true, valores), e);
			throw new ApplicationBusinessException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, e, false, valores));
		}
		
		if (work.getException() != null){
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(codAcom,
							dthrInternacao);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							work.getException(), true, valores), work.getException());
			throw new ApplicationBusinessException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, work.getException(), false, valores));
		}
		
		
		return vo.getValAcom();
	}
	
	
	public Integer calcularConta(final Integer conta, final Short cnvCodigo,
			final String desconto, final String usuarioLogado) throws ApplicationBusinessException {
		final ArrayList<Integer> retornoLista = new ArrayList<Integer>();
		final String nomeObjeto = ObjetosBancoOracleEnum.FFC_CALCULO_CONTA.toString();
		
		AghWork work = new AghWork(usuarioLogado) {
			CallableStatement cs =  null;
			@Override
			public void executeAghProcedure(final Connection connection) throws SQLException {
				
		        try{
		        	this.cs = connection.prepareCall("{call " + nomeObjeto +"(?,?,?,?)}");

			        final Integer retorno = 0;
			        final String descontoPadrao = "N";
			        // Bind dos parametros de entrada/saida
			        CoreUtil.configurarParametroCallableStatement(this.cs, 1, Types.INTEGER, conta);
			        CoreUtil.configurarParametroCallableStatement(this.cs, 2, Types.INTEGER, cnvCodigo);
			        CoreUtil.configurarParametroCallableStatement(this.cs, 3, Types.INTEGER, retorno);
			
			        if(StringUtils.isBlank(desconto)){
			        	CoreUtil.configurarParametroCallableStatement(this.cs, 4, Types.VARCHAR, descontoPadrao);
			        } else {
			        	CoreUtil.configurarParametroCallableStatement(this.cs, 4, Types.VARCHAR, desconto);	
			        }
					
			        this.cs.registerOutParameter(3, Types.INTEGER);
	
			        this.cs.execute();
			        retornoLista.add(this.cs.getInt(3));
			    }
			    finally {
					if (this.cs != null) {
						this.cs.close();
					}
				}
			}
		};
		try {
			doWork(work);
		} catch (final Exception e) {
			final String msg = e.getCause().getMessage().toUpperCase();
			// Tratamento de string, pois é lançada uma exceção de negócio no
			// PL/SQ, porém sem um código (Ex.: AGH-00001)
			if (msg.indexOf("CONTA") > -1 && msg.indexOf("ENCERRADA") > -1) {
				throw new ApplicationBusinessException(
						AinAcompanhantesInternacaoDAOxceptionCode.CONTA_ENCERRADA);
			} else {
				final String valores = CoreUtil
						.configurarValoresParametrosCallableStatement(conta,
								cnvCodigo, 0, desconto);
				LOG.error(CoreUtil
						.configurarMensagemUsuarioLogCallableStatement(
								nomeObjeto, e, true, valores), e);
				throw new ApplicationBusinessException(
						AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
						nomeObjeto, valores, CoreUtil
								.configurarMensagemUsuarioLogCallableStatement(
										nomeObjeto, e, false, valores));
			}
		}
		
		if (work.getException() != null){
			final String msg = work.getException().getMessage().toUpperCase();
			// Tratamento de string, pois é lançada uma exceção de negócio no
			// PL/SQ, porém sem um código (Ex.: AGH-00001)
			if (msg.indexOf("CONTA") > -1 && msg.indexOf("ENCERRADA") > -1) {
				throw new ApplicationBusinessException(
						AinAcompanhantesInternacaoDAOxceptionCode.CONTA_ENCERRADA);
			} else {
				final String valores = CoreUtil
						.configurarValoresParametrosCallableStatement(conta,
								cnvCodigo, 0, desconto);
				LOG.error(CoreUtil
						.configurarMensagemUsuarioLogCallableStatement(
								nomeObjeto, work.getException(), true, valores), work.getException());
				throw new ApplicationBusinessException(
						AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
						nomeObjeto, valores, CoreUtil
								.configurarMensagemUsuarioLogCallableStatement(
										nomeObjeto, work.getException(), false, valores));
			}
		}
		retornoLista.add(Integer.valueOf("0"));
		return retornoLista.get(0);
	}
	
	public ValidaContaTrocaConvenioVO validarContaTrocaConvenio(
			final Integer internacaoSeq, final Date dataInternacao,
			final Short cnvCodigo, final Integer phiSeq, final String usuarioLogado)
			throws ApplicationBusinessException {
		final ValidaContaTrocaConvenioVO vo = new ValidaContaTrocaConvenioVO();
		final String nomeObjeto = EsquemasOracleEnum.AGH + "."
				+ ObjetosBancoOracleEnum.FATP_AGHU_CTH_P_T_C;
		
		AghWork work = 
				new AghWork(usuarioLogado) {
			CallableStatement cs = null;

			@Override
			public void executeAghProcedure(final Connection connection)
					throws SQLException {
				
				try {
					this.cs = connection.prepareCall("{call "+ 
							nomeObjeto + "(?,?,?,?,?)}");

					// Bind dos parametros de entrada/saida 
					final Integer retorno = 0;
					CoreUtil.configurarParametroCallableStatement(
							this.cs, 1, Types.INTEGER, internacaoSeq);
					CoreUtil.configurarParametroCallableStatement(
							this.cs, 2, Types.TIMESTAMP, dataInternacao);
					CoreUtil.configurarParametroCallableStatement(
							this.cs, 3, Types.INTEGER, cnvCodigo);
					CoreUtil.configurarParametroCallableStatement(
							this.cs, 4, Types.INTEGER, phiSeq);
					CoreUtil.configurarParametroCallableStatement(
							this.cs, 5, Types.INTEGER, retorno);

					this.cs.registerOutParameter(4, Types.INTEGER);
					this.cs.registerOutParameter(5, Types.INTEGER);

					this.cs.execute();
					
					vo.setProcedimentoHospitalarInternoSeq(this.cs
							.getInt(4));
					vo.setRetorno(this.cs.getInt(5));

				} catch (final SQLException se) {
					LOG.error(se.getMessage(), se);
				} catch (final Exception e) {
					LOG.error(e.getMessage(), e);
				} finally {
					if (this.cs != null) {
						this.cs.close();
					}
				}
			}
		};
		
		try {
			doWork(work);
		} catch (final Exception e) {
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(
							internacaoSeq, dataInternacao, cnvCodigo, phiSeq, 0);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							e, true, valores), e);
			throw new ApplicationBusinessException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, e, false, valores));
		}
		
		if (work.getException() != null){
			final String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(
							internacaoSeq, dataInternacao, cnvCodigo, phiSeq, 0);
			LOG.error(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							work.getException(), true, valores), work.getException());
			throw new ApplicationBusinessException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, work.getException(), false, valores));
		}
		return vo;
	}
	
	
	public Double calcularValorAcomodacaoConvenio(
			final Integer codigoAcomodacao, final Short codigoConvenio,
			final Byte codigoPlano, final Date data,
			final Integer numeroAcompanhantes, final String usuarioLogado) throws ApplicationBusinessException {
		
		// Classe local somente para retornar valor da FUNCTION
				class ValoresVO {
					private Double valor;
					
					public void setValor(final Double valor) {
						this.valor = valor;
					}
					
					public Double getValor() {
						return this.valor;
					}
				}
				final ValoresVO vo = new ValoresVO();
				final String nomeObjeto = ObjetosBancoOracleEnum.FFC_F_VALR_ACOM_CNVN.toString();
				
				AghWork work = new AghWork(usuarioLogado) {
					CallableStatement cs = null;
					
					@Override
					public void executeAghProcedure(final Connection connection) throws SQLException {
						
						try {
							this.cs = connection.prepareCall("{? = call "
									+ nomeObjeto + "(?,?,?,?,?)}");

							// Bind dos parametros de entrada/saida
							CoreUtil.configurarParametroCallableStatement(this.cs, 2,
									Types.INTEGER, codigoAcomodacao);
							CoreUtil.configurarParametroCallableStatement(this.cs, 3,
									Types.INTEGER, codigoConvenio);
							CoreUtil.configurarParametroCallableStatement(this.cs, 4,
									Types.INTEGER, codigoPlano);
							CoreUtil.configurarParametroCallableStatement(this.cs, 5,
									Types.DATE, data);
							CoreUtil.configurarParametroCallableStatement(this.cs, 6,
									Types.INTEGER, numeroAcompanhantes);

							this.cs.registerOutParameter(1, Types.DOUBLE);

							this.cs.execute();
							
							vo.setValor(this.cs.getDouble(1));
							
						} finally {
							if (this.cs != null) {
								this.cs.close();
							}
						}
					}
				};
				
				try {
					doWork(work);
				} catch(final Exception e) {
					final String valores = CoreUtil
							.configurarValoresParametrosCallableStatement(
									codigoAcomodacao, codigoConvenio, codigoPlano,
									data, numeroAcompanhantes);
					LOG.error(CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
									e, true, valores), e);
					throw new ApplicationBusinessException(
							AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
							nomeObjeto, valores, CoreUtil
									.configurarMensagemUsuarioLogCallableStatement(
											nomeObjeto, e, false, valores));
				}
				
				if (work.getException() != null){
					final String valores = CoreUtil
							.configurarValoresParametrosCallableStatement(
									codigoAcomodacao, codigoConvenio, codigoPlano,
									data, numeroAcompanhantes);
					LOG.error(CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
									work.getException(), true, valores), work.getException());
					throw new ApplicationBusinessException(
							AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
							nomeObjeto, valores, CoreUtil
									.configurarMensagemUsuarioLogCallableStatement(
											nomeObjeto, work.getException(), false, valores));
				}
				
				return vo.getValor();
		
	}
	
	
}
