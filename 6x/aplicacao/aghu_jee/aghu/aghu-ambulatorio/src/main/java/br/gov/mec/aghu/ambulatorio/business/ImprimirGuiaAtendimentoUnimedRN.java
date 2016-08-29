package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jdbc.Work;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaGuiaAtendimentoUnimedVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioGuiaAtendimentoUnimedVO;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.faturamento.dao.TipoItemDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.TipoItem;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * RN de #43088 Imprimir Guia de atendimento da Unimed
 * 
 * @author aghu
 *
 */
@Stateless
public class ImprimirGuiaAtendimentoUnimedRN extends BaseBusiness {

	private static final long serialVersionUID = -4092343424239737387L;

	private static final Log LOG = LogFactory.getLog(ImprimirGuiaAtendimentoUnimedRN.class);

	private static final Integer CODIGO_IBGE_PORTO_ALEGRE = 431490;
	private static final String PORTO_ALEGRE = "PORTO ALEGRE";

	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
    private IPermissionService permissionService;
	
	@EJB
	private ImprimirGuiaAtendimentoUnimedON imprimirGuiaAtendimentoUnimedON;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject 
	private TipoItemDAO tipoItemDAO;
	
	private static final String CALL = "{? = call ";
	
	public List<RelatorioGuiaAtendimentoUnimedVO> imprimirGuiaAtendimentoUnimed(Integer conNumero) throws ApplicationBusinessException {

		List<RelatorioGuiaAtendimentoUnimedVO> retorno = new ArrayList<RelatorioGuiaAtendimentoUnimedVO>();
		List<ConsultaGuiaAtendimentoUnimedVO> listaGuiaAtdUnimed = this.obterConsultaGuiaAtendimentoUnimed(conNumero);
		
		if (listaGuiaAtdUnimed != null && listaGuiaAtdUnimed.size() > 0) {
			
			retorno.add(this.imprimirGuiaAtendimentoUnimedON.montarGuiaATendimentoUnimed(listaGuiaAtdUnimed, conNumero));

		}
		return retorno;
	}
	
	private List<ConsultaGuiaAtendimentoUnimedVO> obterConsultaGuiaAtendimentoUnimed(Integer conNumero) throws ApplicationBusinessException {
		List<ConsultaGuiaAtendimentoUnimedVO> listaGuiaAtdUnimed = this.aacConsultasDAO.pesquisarGuiaAtendimentoUnimed(conNumero);
		List<ConsultaGuiaAtendimentoUnimedVO> listaRetorno = new ArrayList<ConsultaGuiaAtendimentoUnimedVO>();
		
		if (listaGuiaAtdUnimed != null && listaGuiaAtdUnimed.size() > 0) {
			List<TipoItem> listaTipoItens = this.tipoItemDAO.listarTodos();
			
			for (ConsultaGuiaAtendimentoUnimedVO consultaGuiaAtendimentoUnimedVO : listaGuiaAtdUnimed) {
				for (TipoItem tipoItem : listaTipoItens) {	
					
					Short tpitCod = this.ffcFItemConvCsp(consultaGuiaAtendimentoUnimedVO.getPrhcCodHcpa(), 
							consultaGuiaAtendimentoUnimedVO.getCspCnvCodigo(), consultaGuiaAtendimentoUnimedVO.getCspSeq(), 
							consultaGuiaAtendimentoUnimedVO.getTpconvTpitCod());
					
					if (tipoItem.getCod() == tpitCod) {
						listaRetorno.add(consultaGuiaAtendimentoUnimedVO);
						break;
					}
				}
			}
		}
		return listaRetorno;
	}

	/**
	 * ORADB FFC_F_MATRICULA_PROF - chamada nativa
	 * 
	 * @param atdSeq
	 * @param mexSeq
	 * @param cspCnvCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String ffcFMatriculaProf(final Integer atdSeq, final Integer mexSeq, final Short cspCnvCodigo) throws ApplicationBusinessException {

		if (!isHCPA() || !aacConsultasDAO.isOracle()) {
			return null;
		}
		String retorno = null;				
		final String nomeObjeto = ObjetosBancoOracleEnum.FFC_F_MATRICULA_PROF.toString();
		final List<String> result = new ArrayList<String>();
		try {
			aacConsultasDAO.doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					CallableStatement cs = null;
					try {
						cs = connection.prepareCall(CALL + nomeObjeto + "(?,?,?)}");
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, atdSeq);
						CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER, mexSeq);
						CoreUtil.configurarParametroCallableStatement(cs, 4, Types.INTEGER, cspCnvCodigo);
						cs.registerOutParameter(1, Types.VARCHAR);						
						cs.execute();
						result.add(cs.getString((1)));
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			});
			retorno = result.get(0);
		} catch (Exception e) {
			String valores = CoreUtil.configurarValoresParametrosCallableStatement(atdSeq, mexSeq, cspCnvCodigo);
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,	nomeObjeto, 
					valores, CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}
		return retorno;
	}

	/**
	 * ORADB FFC_F_COD_OPERADORA - chamada nativa
	 * 
	 * @param cspCnvCodigo
	 * @param cspSeq
	 * @param nro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Integer ffcFCodOperadora(final Short cspCnvCodigo, final Byte cspSeq, final Integer nro) throws ApplicationBusinessException {
			
	if (!isHCPA() || !aacConsultasDAO.isOracle()) {
		return null;
	}
	Integer retorno = null;				
	final String nomeObjeto = ObjetosBancoOracleEnum.FFC_F_COD_OPERADORA.toString();
	final List<Integer> result = new ArrayList<Integer>();
	try {
		aacConsultasDAO.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				CallableStatement cs = null;
				try {
					cs = connection.prepareCall(CALL + nomeObjeto + "(?,?,?)}");
					CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, cspCnvCodigo);
					CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER, cspSeq);
					CoreUtil.configurarParametroCallableStatement(cs, 4, Types.INTEGER, nro);
					cs.registerOutParameter(1, Types.INTEGER);						
					cs.execute();
					result.add(cs.getInt((1)));
				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		});
		retorno = result.get(0);
	} catch (Exception e) {
		String valores = CoreUtil.configurarValoresParametrosCallableStatement(cspCnvCodigo, cspSeq, nro);
		throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,	nomeObjeto, 
				valores, CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
	}
	return retorno;
}
	
	/**
	 * ORADB FFC_F_ITEM_CONV_CSP - chamada nativa
	 * 
	 * @param prhcCodHcpa
	 * @param cspCnvCodigo
	 * @param cspSeq
	 * @param tpconvTpitCod
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Short ffcFItemConvCsp(final Integer prhcCodHcpa, final Short cspCnvCodigo, final Byte cspSeq, final Short tpconvTpitCod) 
				throws ApplicationBusinessException {
				
		if (!isHCPA() || !aacConsultasDAO.isOracle()) {
			return null;
		}
		Integer retorno = null;				
		final String nomeObjeto = ObjetosBancoOracleEnum.FFC_F_ITEM_CONV_CSP.toString();
		final List<Integer> result = new ArrayList<Integer>();
		try {
			aacConsultasDAO.doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					CallableStatement cs = null;
					try {
						cs = connection.prepareCall(CALL + nomeObjeto + "(?,?,?,?)}");
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, prhcCodHcpa);
						CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER, cspCnvCodigo);
						CoreUtil.configurarParametroCallableStatement(cs, 4, Types.INTEGER, cspSeq);
						CoreUtil.configurarParametroCallableStatement(cs, 5, Types.INTEGER, tpconvTpitCod);
						cs.registerOutParameter(1, Types.INTEGER);						
						cs.execute();
						result.add(cs.getInt((1)));
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			});
			retorno = result.get(0);
		} catch (Exception e) {
			String valores = CoreUtil.configurarValoresParametrosCallableStatement(prhcCodHcpa, cspCnvCodigo, cspSeq, tpconvTpitCod);
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,	nomeObjeto, 
					valores, CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}
		return retorno.shortValue();
	}
	
	/**
	 * ORADB FFC_F_BUSCA_SENHA - chamada nativa
	 * 
	 * 
	 * @param ctaNro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String ffcFBuscaSenha(final Integer ctaNro) throws ApplicationBusinessException {

		if (!isHCPA() || !aacConsultasDAO.isOracle()) {
			return null;
		}
		String retorno = null;				
		final String nomeObjeto = ObjetosBancoOracleEnum.FFC_F_BUSCA_SENHA.toString();
		final List<String> result = new ArrayList<String>();
		try {
			aacConsultasDAO.doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					CallableStatement cs = null;
					try {
						cs = connection.prepareCall(CALL + nomeObjeto + "(?)}");
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, ctaNro);
						cs.registerOutParameter(1, Types.VARCHAR);						
						cs.execute();
						result.add(cs.getString((1)));
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			});
			retorno = result.get(0);
		} catch (Exception e) {
			String valores = CoreUtil.configurarValoresParametrosCallableStatement(ctaNro);
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,	nomeObjeto, 
					valores, CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}
		return retorno;
	}
	
	/**
	 * ORADB FFC_F_BUSCA_DATA_SENHA - chamada nativa
	 * 
	 * @param ctaNro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String ffcFBuscaDataSenha(final Integer ctaNro) throws ApplicationBusinessException {

		if (!isHCPA() || !aacConsultasDAO.isOracle()) {
			return null;
		}
		String retorno = null;				
		final String nomeObjeto = ObjetosBancoOracleEnum.FFC_F_BUSCA_DATA_SENHA.toString();
		final List<String> result = new ArrayList<String>();
		try {
			aacConsultasDAO.doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					CallableStatement cs = null;
					try {
						cs = connection.prepareCall(CALL + nomeObjeto + "(?)}");
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, ctaNro);
						cs.registerOutParameter(1, Types.VARCHAR);						
						cs.execute();
						result.add(cs.getString((1)));
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			});
			retorno = result.get(0);
		} catch (Exception e) {
			String valores = CoreUtil.configurarValoresParametrosCallableStatement(ctaNro);
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,	nomeObjeto, 
					valores, CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}
		return retorno;
	}
	
	/**
	 * ORADB RAPC_VESETEM_UNIMED - chamada nativa
	 * 
	 * @param matriculaConsultado
	 * @param vinCodigoConsultado
	 * @param depCodigo
	 * @param prontuario
	 * @param data
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String rapcVesetemUnimed(final Integer matriculaConsultado, final Short vinCodigoConsultado, final Integer depCodigo, 
			final Integer prontuario, final Date data) throws ApplicationBusinessException {

		if (!isHCPA() || !aacConsultasDAO.isOracle()) {
			return null;
		}
		String retorno = null;				
		final String nomeObjeto = ObjetosBancoOracleEnum.RAPC_VESETEM_UNIMED.toString();
		final List<String> result = new ArrayList<String>();
		try {
			aacConsultasDAO.doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					CallableStatement cs = null;
					try {
						cs = connection.prepareCall(CALL + nomeObjeto + "(?,?,?,?,?)}");
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, matriculaConsultado);
						CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER, vinCodigoConsultado);
						CoreUtil.configurarParametroCallableStatement(cs, 4, Types.INTEGER, depCodigo);
						CoreUtil.configurarParametroCallableStatement(cs, 5, Types.INTEGER, prontuario);
						CoreUtil.configurarParametroCallableStatement(cs, 6, Types.DATE, data);
						cs.registerOutParameter(1, Types.VARCHAR);						
						cs.execute();
						result.add(cs.getString((1)));
						
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			});
			retorno = result.get(0);
		} catch (Exception e) {
			String valores = CoreUtil.configurarValoresParametrosCallableStatement(matriculaConsultado, vinCodigoConsultado, depCodigo, prontuario, data);
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,	nomeObjeto, 
					valores, CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}
		return retorno;
	}
	
	/**
	 * ORADB FUNCTION AIPC_GET_CARTAO_SUS
	 * 
	 * @param codigoPaciente
	 */
	public BigInteger obterCartaoSus(Integer prontuario) {
		BigInteger numeroCartaoSus = this.aipPacientesDAO.obterCartaoSus(prontuario);
		return numeroCartaoSus != null ? numeroCartaoSus : BigInteger.ZERO;
	}
	
	/**
	 * ORADB FUNCTION RAPC_COD_IBGE
	 * 
	 * @param nomeCidade
	 */
	public Integer obterCodigoIbgePorNomeCidade(String nomeCidade) {
		nomeCidade = StringUtils.trim(nomeCidade);
		Integer retorno = null;
		if (StringUtils.equalsIgnoreCase(PORTO_ALEGRE, nomeCidade)) {
			retorno = CODIGO_IBGE_PORTO_ALEGRE;
		} else {
			List<AipCidades> cidades = this.cadastroPacienteFacade.pesquisarCidadesPorNome(nomeCidade);
			if (!cidades.isEmpty()) {
				return cidades.get(0).getCodIbge();
			} else {
				return CODIGO_IBGE_PORTO_ALEGRE;
			}
		}
		return retorno;
	}
	
	public Boolean verificarConvenioUnimed(Integer conNumero, Boolean vTemp) throws ApplicationBusinessException {
		AghParametros paramConvenio = null, paramPlano = null;
		
        paramConvenio = parametroFacade.obterAghParametro(AghuParametrosEnum.P_UNIMED_FUNC_CNV);
        paramPlano = parametroFacade.obterAghParametro(AghuParametrosEnum.P_UNIMED_FUNC_PL);
        if (paramConvenio != null && paramPlano != null) {
        	if (aacConsultasDAO.verificarSeConsultaPossuiConvenioSaudePlano(conNumero, Short.valueOf(paramConvenio.getVlrNumerico().toString()), 
        			paramPlano.getVlrNumerico().byteValue()) && 
        			this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "imprimirGuiaUnimed", "imprimir")) {
        		vTemp = true;
        		
        	}
        }
        return vTemp;
    }

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
}
