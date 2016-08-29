package br.gov.mec.aghu.registrocolaborador.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapRamaisCentroCusto;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.dao.VRapPessoaServidorDAO;
import br.gov.mec.aghu.registrocolaborador.exceptioncode.ServidorRNExceptionCode;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RapServidoresRN extends BaseBusiness {

	
	private static final Log LOG = LogFactory.getLog(RapServidoresRN.class);
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 455860698676930556L;

	@Inject
    private RapServidoresDAO rapServidoresDAO;
	
	@Inject
	private VRapPessoaServidorDAO vRapPessoaServidorDAO;
		
	@EJB
	RapServidorUnimedRN rapServidorUnimedRN;

	@EJB
	private RapServidoresRN rapServidoresRN;
	
	@EJB
	private RapRamaisCentroCustoRN rapRamaisCentroCustoRN;
		
	public RapRamaisCentroCustoRN getRapRamaisCentroCustoRN() {
		return rapRamaisCentroCustoRN;
	}
	
	public RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}
	
	/**
	 * ORADB: Procedure RAPK_SER_RN.RN_SERP_VER_VINCULO
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException
	 */
	public void verificarVinculoServidor(RapServidores servidor)
			throws ApplicationBusinessException {

		Calendar dataAtual = Calendar.getInstance();	
		dataAtual.setTime(DateUtil.obterDataComHoraInical(null));
		
		if (servidor == null || servidor.getVinculo() == null ) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		
		if (servidor.getVinculo().getIndSituacao() != DominioSituacao.A) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_VINCULO_INATIVO);

		}

		if (servidor.getVinculo().getIndCcustLotacao() == DominioSimNao.S
				&& servidor.getCentroCustoLotacao() == null) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_INFORMAR_CCUSTO_LOTACAO);
		}
		
		if (servidor.getVinculo().getIndHorario() == DominioSimNao.S
				&& servidor.getHorarioTrabalho() == null) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_INFORMAR_HORARIO);
		}

		if (servidor.getVinculo().getIndOcupacao() == DominioSimNao.S
				&& servidor.getOcupacaoCargo() == null) {
			throw new ApplicationBusinessException(
					ServidorRNExceptionCode.MENSAGEM_INFORMAR_OCUPACAO);
		}

		if (servidor.getDtInicioVinculo().after(dataAtual.getTime())) {

			Integer nroDiasAdmissao = servidor.getVinculo()
					.getNroDiasAdmissao();

			if (nroDiasAdmissao != null && nroDiasAdmissao > 0) {

				dataAtual.add(Calendar.DAY_OF_MONTH, nroDiasAdmissao);

				if (servidor.getDtInicioVinculo().after(dataAtual.getTime())) {
					throw new ApplicationBusinessException(
							ServidorRNExceptionCode.MENSAGEM_DATA_INICIO_SUPERIOR);
				}

			} else {
				throw new ApplicationBusinessException(
						ServidorRNExceptionCode.MENSAGEM_DATA_INICIO_POSTERIOR_DATA_ATUAL);
			}

		}

	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public RapServidoresVO pesquisarProfissionalPorServidor(RapServidores servidor) {
		return rapServidoresDAO.pesquisarProfissionalPorServidor(servidor);
	}
	
	public List<RapServidoresVO> pesquisarProfissionaisPorVinculoMatriculaNome(String parametro, Integer count) {
		return rapServidoresDAO.pesquisarProfissionaisPorVinculoMatriculaNome(parametro, count);
	}
	
	public Long pesquisarProfissionaisPorVinculoMatriculaNomeCount(String strPesquisa, Integer count) {
		return rapServidoresDAO.pesquisarProfissionaisPorVinculoMatriculaNomeCount(strPesquisa, count);
	}
	
	/**
	 * regras da #8990
	 * 
	 *  
	 * valida base e local de operação do sistema 
	 */	
	public boolean isHospitalHCPA(){
		
		if(getRapServidoresDAO().isOracle() && isHCPA()){
			
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * montagem da consulta principal 
	 */
	public List<RapServidoresVO> pesquisarFuncionarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			RapServidores filtro) {
		List<RapServidoresVO> retorno = getRapServidoresDAO().pesquisarFuncionarios(firstResult, maxResult, orderProperty, asc, filtro);
				
		for (RapServidoresVO rapServidores : retorno) {
			
			rapServidores.setRamalChefiaCCLotacao(obterRamalChefia(rapServidores.getCodigoCentroCustoLotacao(), true));
			rapServidores.setRamalChefiaCCAtuacao(obterRamalChefia(rapServidores.getCodigoCentroCustoAtuacao(), false));
			
			if(isHospitalHCPA() && rapServidores.getCodStarh() != null){
				rapServidores.setCarteiraUnimed(pesquisarCarteirasUnimedFuncionarios(rapServidores.getCodStarh()));
			}
			
			rapServidores.setSituacaoFuncionario(obterSituacaoFuncionario(rapServidores));
		}
		
		return retorno;
	}
	
	/**
	 * preenche ramal da chefia do centro de custo de lotação e atuação
	 */
	public Integer obterRamalChefia(Integer codigoCC, boolean lotacao) {
		List<RapServidores> consulta1 = getRapServidoresDAO().consultarRamalChefia1(codigoCC, lotacao);
		
		if(consulta1 != null && !consulta1.isEmpty() && consulta1.get(0) 
				!= null && consulta1.get(0).getRamalTelefonico() != null){
			
				return consulta1.get(0).getRamalTelefonico().getNumeroRamal();
			
		} else {
			RapRamaisCentroCusto consulta2 = getRapRamaisCentroCustoRN().consultarRamalChefia2e3(codigoCC, true);
			if(consulta2 != null){
				return Integer.valueOf(consulta2.getId().getRamNroRamal());	
			} else {
				RapRamaisCentroCusto consulta3 = getRapRamaisCentroCustoRN().consultarRamalChefia2e3(codigoCC, false);
				if (consulta3 != null) {
					return Integer.valueOf(consulta3.getId().getRamNroRamal());
				}
			}
		}
		return null;
	}	
	
	/**
	 * resgata o número de carteira do funcionário 
	 */
	public String pesquisarCarteirasUnimedFuncionarios(Integer codStarh){
		return getRapServidorUnimedRN().pesquisarCarteirasUnimedFuncionarios(codStarh);		
	}
	
	/**
	 * obtém situação do funcionário baseado na data de término 
	 */
	public boolean obterSituacaoFuncionario(RapServidoresVO funcionario){
		
		if(funcionario !=null && funcionario.getDtFimVinculo() == null){
			return true;
		}
		else if(funcionario !=null && (funcionario.getIndSituacao().isAtivo() 
				|| funcionario.getIndSituacao().isProgramado()) 
				&& (DateUtil.validaDataMaiorIgual(DateUtil.truncaData(funcionario.getDtFimVinculo()), DateUtil.truncaData(new Date())))){
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	
	public Servidor obterVRapPessoaServidorPorVinCodigoMatricula(Integer matricula, Short vinCodigo) {
		Servidor servidor = new Servidor();
		Object[] item = this.vRapPessoaServidorDAO.obterVRapPessoaServidorPorVinCodigoMatricula(matricula, vinCodigo); 
		if(item == null) {
			return null;
		}
		servidor.setVinculo((Short)item[0]);
		servidor.setMatricula((Integer)item[1]);
		servidor.setNomePessoaFisica((String)item[2]);
		servidor.setNomeUsual((String)item[3]);
		servidor.setIndSituacao((String)item[4]);
		return servidor;
	}
	
	public RapServidoresRN getRapServidoresRN() {
		return rapServidoresRN;
	}

	public void setRapServidoresRN(RapServidoresRN rapServidoresRN) {
		this.rapServidoresRN = rapServidoresRN;
	}

	public RapServidorUnimedRN getRapServidorUnimedRN() {
		return rapServidorUnimedRN;
	}

	public void setRapServidorUnimedRN(RapServidorUnimedRN rapServidorUnimedRN) {
		this.rapServidorUnimedRN = rapServidorUnimedRN;
	}
}
