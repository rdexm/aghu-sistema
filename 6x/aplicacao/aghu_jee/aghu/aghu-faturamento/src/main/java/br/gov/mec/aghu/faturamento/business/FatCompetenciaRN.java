package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Order;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatCompetenciaRN extends BaseBusiness {

	@EJB
	private FaturamentoFatkCpeRN faturamentoFatkCpeRN;
	
	private static final Log LOG = LogFactory.getLog(FatCompetenciaRN.class);
	
	public enum FatCompetenciaRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_EXCECAO_ERRO_EXCLUIR_COMPETENCIA, MENSAGEM_EXCECAO_ERRO_ATUALIZAR_COMPETENCIA, MENSAGEM_EXCECAO_ERRO_NAO_EXISTE_COMP_ANT_MANUTENCAO;
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatCompetenciaDAO fatCompetenciaDAO;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5478452331162778759L;

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	/**
	 * Método para implementar regras da trigger executada antes da inserção
	 * de <code>fattCpeBri</code>.
	 * 
	 * ORADB Trigger FATT_CPE_BRI
	 *  
	 * 
	 */
	private void fattCpeBri(final FatCompetencia fatComp) throws ApplicationBusinessException {
		if( !getFaturamentoFatkCpeRN().rnCpepVerDatas(fatComp.getId().getDtHrInicio(), fatComp.getDtHrFim()) ){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			fatComp.setCriadoEm(new Date());
			fatComp.setAlteradoEm(new Date());
			fatComp.setCriadoPor(servidorLogado.getUsuario());
			fatComp.setAlteradoPor(servidorLogado.getUsuario());
		}
	}
	
	/**
	 * Insere um AIH executando suas respectivas triggers
	 * 
	 * @param fatCompetencia
	 *  
	 */
	public void inserirFatCompetencia(final FatCompetencia fatCompetencia, boolean flush) throws ApplicationBusinessException {
		fattCpeBri(fatCompetencia);
		getFatCompetenciaDAO().persistir(fatCompetencia);
		if (flush){
			getFatCompetenciaDAO().flush();
		}
	}
	
	public void excluirFatCompetencia(FatCompetencia comp) {
		getFatCompetenciaDAO().remover(comp);
	}
	
	/**
	 * Método para implementar regras da trigger executada antes da atualização
	 * de <code>fattCpeBru</code>.
	 * 
	 * ORADB Trigger FATT_CPE_BRU
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void fattCpeBru(final FatCompetencia fatCompetencia) throws ApplicationBusinessException {
		if( !getFaturamentoFatkCpeRN().rnCpepVerDatas(fatCompetencia.getId().getDtHrInicio(), fatCompetencia.getDtHrFim()) ){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			fatCompetencia.setAlteradoEm(new Date());
			fatCompetencia.setAlteradoPor(servidorLogado.getUsuario());
		}
	}
	
	/**
	 * Atualiza uma FatCompetencia executando suas respectivas triggers
	 * 
	 * @param fatAih
	 * @param oldFatAih
	 *  
	 */
	public void atualizarFatCompetencia(final FatCompetencia fatCompetencia) throws ApplicationBusinessException {
		fattCpeBru(fatCompetencia);
		getFatCompetenciaDAO().atualizar(fatCompetencia);
	}

	public void estornarCompetenciaInternacao(FatCompetencia competencia) throws ApplicationBusinessException {

		FatCompetencia ultimaCompetencia = obterCompetenciaAnteriorModInternacao();

		if(ultimaCompetencia != null){
			try {
				FatCompetencia competenciaExcluir = getFatCompetenciaDAO().obterCompetenciaModuloMesAno(competencia.getId().getModulo(), competencia.getId().getMes(), competencia.getId().getAno());
				excluirFatCompetencia(competenciaExcluir);
			} catch (Exception e) {
				throw new ApplicationBusinessException(FatCompetenciaRNExceptionCode.MENSAGEM_EXCECAO_ERRO_EXCLUIR_COMPETENCIA, e.getMessage());
			}
			
			ultimaCompetencia.setDtHrFim(null);
			ultimaCompetencia.setDthrLiberadoEmerg(null);
			ultimaCompetencia.setDthrLiberadoCo(null);
			ultimaCompetencia.setIndFaturado(Boolean.FALSE);
			ultimaCompetencia.setIndSituacao(DominioSituacaoCompetencia.A);
			
			try {
				atualizarFatCompetencia(ultimaCompetencia);
			} catch (Exception e) {
				throw new ApplicationBusinessException(FatCompetenciaRNExceptionCode.MENSAGEM_EXCECAO_ERRO_ATUALIZAR_COMPETENCIA, e.getMessage());
			}
			
		} else {
			throw new ApplicationBusinessException(FatCompetenciaRNExceptionCode.MENSAGEM_EXCECAO_ERRO_NAO_EXISTE_COMP_ANT_MANUTENCAO);
		}
	}

	private FatCompetencia obterCompetenciaAnteriorModInternacao() {

		Order[] ordens = { Order.asc(FatCompetencia.Fields.MODULO.toString()),
				Order.desc(FatCompetencia.Fields.ANO.toString()),
				Order.desc(FatCompetencia.Fields.MES.toString()),
				Order.desc(FatCompetencia.Fields.DT_HR_INICIO.toString()) };
		
		return validarListaCompetencias(getFatCompetenciaDAO().obterCompetenciasPorModuloESituacoes(DominioModuloCompetencia.INT, ordens, DominioSituacaoCompetencia.M));

	}
	
	/**
	 * Irá obter a última competência do módulo de internação, que estará em situação aberta ou em manutenção
	 * 
	 * @return FatCompetencia
	 */
	public FatCompetencia obterUltimaCompetenciaModInternacao() {
		return validarListaCompetencias(getFatCompetenciaDAO().
				obterCompetenciasPorModuloESituacoes(DominioModuloCompetencia.INT, DominioSituacaoCompetencia.A, DominioSituacaoCompetencia.M));
	}

	private FatCompetencia validarListaCompetencias(final List<FatCompetencia> competencias) {
		if(competencias != null && !competencias.isEmpty()){
			return competencias.get(0);
		}
		return null;
	}
	
	protected FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}

	protected FaturamentoFatkCpeRN getFaturamentoFatkCpeRN(){
		return faturamentoFatkCpeRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
