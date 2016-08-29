package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioMateriaisColetarInternacaoFiltroVO;
import br.gov.mec.aghu.exames.vo.SolicitacaoColetarVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Responsavel pelas regras de negocio do relatório de Materiais a coletar na Internação.
 * 
 * @author fwinck
 *
 */
@Stateless
public class RelatorioMateriaisColetarRN extends BaseBusiness {


@EJB
private ItemSolicitacaoExameRN itemSolicitacaoExameRN;

@EJB
private EtiquetasON etiquetasON;

@EJB
private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

private static final Log LOG = LogFactory.getLog(RelatorioMateriaisColetarRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@EJB
private IBancoDeSangueFacade bancoDeSangueFacade;

@Inject
private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

@EJB
private IAghuFacade aghuFacade;

@EJB
private IServidorLogadoFacade servidorLogadoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -310033162942555805L;

	public enum RelatorioMateriaisColetarRNExceptionCode implements BusinessExceptionCode {
		MSG_VALIDA_PARAMS_REPORT;
	}
 
	public AghUnidadesFuncionais buscarAghUnidadesFuncionaisPorParametro(String valor) {
		return getAghuFacade().buscarAghUnidadesFuncionaisPorParametro(valor);
	}

	public List<SolicitacaoColetarVO> buscaMateriaisColetarInternacao(RelatorioMateriaisColetarInternacaoFiltroVO filtro, String nomeMicrocomputador)throws BaseException{
		beforeReport(filtro);

		List<SolicitacaoColetarVO> listVo = getAelSolicitacaoExameDAO().buscaMateriaisColetarInternacao(filtro);
		afterReport(listVo, filtro, nomeMicrocomputador);
		if(filtro.getIndImpressaoEtiquetas() != null && filtro.getIndImpressaoEtiquetas().equals(DominioSimNao.S)){
			chamarEtiquetaBarras(listVo, filtro);
		}

		if(listVo!=null){
			listVo = agruparMontarLista(listVo);
		}

		return listVo;
	}

	private void chamarEtiquetaBarras(List<SolicitacaoColetarVO> listVo, RelatorioMateriaisColetarInternacaoFiltroVO filtro) throws BaseException {
		if(listVo!=null && listVo.size()>0){
			Boolean existeImpressoraPadrao = getCadastrosBasicosInternacaoFacade().verificaExisteImpressoraPadrao(filtro.getUnidadeColeta().getSeq(), TipoDocumentoImpressao.ETIQUETAS_BARRAS);
			List<String> jaImpressos = new LinkedList<String>();
			for (SolicitacaoColetarVO solicitacaoColetarVO : listVo) {
				if(solicitacaoColetarVO.getSoeSeq() != null && !jaImpressos.contains(solicitacaoColetarVO.getSoeSeq())){
					if(filtro.getImpressora()!=null){
						// Impressão de etiquetas considerando a internação
						AelSolicitacaoExames solic = getAelSolicitacaoExameDAO().obterPeloId(Integer.parseInt(solicitacaoColetarVO.getSoeSeq()));
						getEtiquetasON().gerarEtiquetas(true, solic, getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(filtro.getUnidadeColeta().getSeq()), filtro.getImpressora().getFilaImpressora(), null);
					}else if(existeImpressoraPadrao){
						// Deve gerar etiquetas somente se existir impressora padrão cadastrada
						// Impressão de etiquetas considerando a internação
						AelSolicitacaoExames solic = getAelSolicitacaoExameDAO().obterPeloId(Integer.parseInt(solicitacaoColetarVO.getSoeSeq()));
						getEtiquetasON().gerarEtiquetas(true, solic, getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(filtro.getUnidadeColeta().getSeq()), null, null);
					}
					jaImpressos.add(solicitacaoColetarVO.getSoeSeq());
				}
			}
		}
	}

	public void beforeReport(RelatorioMateriaisColetarInternacaoFiltroVO filtro) throws ApplicationBusinessException{
		validaParametrosReport(filtro);
		verificaExecucaoPlantao(filtro);
		setaParametrosRelatorio(filtro);
		setaDataPesquisaRelatorio(filtro);
	}
	
	private IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}	
	
	/**
	 * ORADB procedure CF_nro_unicoFormula
	 * @param listVo
	 * @param filtro
	 * @throws BaseException 
	 */
	public void afterReport(List<SolicitacaoColetarVO> listVo, RelatorioMateriaisColetarInternacaoFiltroVO filtro, String nomeMicrocomputador) throws BaseException{

		AelSitItemSolicitacoes sitItemEmColeta = getAelSitItemSolicitacoesDAO().obterPeloId("EC");
		AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO = getAelItemSolicitacaoExameDAO();
		final Date dataFimVinculoServidor = new Date();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		for (SolicitacaoColetarVO solic : listVo) {

			//RN: 1
			if(	solic.getTipoRegistro()!=null && solic.getTipoRegistro().equals("E")
				&& (filtro.getAelSitItemSolicitacoes() != null && filtro.getAelSitItemSolicitacoes().getCodigo().toUpperCase().equals("AC"))	
				&& (solic.getSitCodigo()!=null && solic.getSitCodigo().toUpperCase().equals("AC"))){

				AelItemSolicitacaoExames itemSolicitacao = getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamesPorIdAColetar(Integer.parseInt(solic.getSoeSeq()), Short.parseShort(solic.getIseSeqp()));
				if(itemSolicitacao!=null){
					itemSolicitacao.setSituacaoItemSolicitacao(sitItemEmColeta);
					getItemSolicitacaoExameRN().atualizar(itemSolicitacao, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
					aelItemSolicitacaoExameDAO.flush();
				}
			}

			//RN: 2
			if(filtro.getpSituacaoAmostra().equalsIgnoreCase("P")){
				AbsSolicitacoesHemoterapicas she = getBancoDeSangueFacade().obterSolicitacaoPorSeqESitColeta(						 
																					solic.getSheSeq(), DominioSituacaoColeta.P);
				if(she!=null){
					she.setIndSituacaoColeta(DominioSituacaoColeta.E);
					she.setDthrSitEmColeta(new Date()); 
					this.getBancoDeSangueFacade().atualizarSolicitacaoHemoterapica(she, nomeMicrocomputador);
				}
			}
		}
	}
	
	private List<SolicitacaoColetarVO> agruparMontarLista(List<SolicitacaoColetarVO> listVo) {
		SolicitacaoColetarVO voPai = null;
		List<SolicitacaoColetarVO> listVoAux = new ArrayList<SolicitacaoColetarVO>();
		for(SolicitacaoColetarVO vo : listVo){
			if(voPai == null || (!vo.getSoeSeq().equals(voPai.getSoeSeq()) || !vo.getDescricao().equals(voPai.getDescricao()) || !vo.getSeqp().equals(voPai.getSeqp()))){
				vo.setList(new ArrayList<SolicitacaoColetarVO>());
				voPai = vo;
				listVoAux.add(voPai);
			}else{
				//listVo.remove(vo);
				voPai.getList().add(vo);
			}
			
		}
		return listVoAux;
	}
	
	

	/**
	 * ORADB EVT_WHEN_BUTTON_PRESSED
	 * @param filtro
	 */
	private void validaParametrosReport(RelatorioMateriaisColetarInternacaoFiltroVO filtro) throws ApplicationBusinessException{
		if(filtro.getAelSolicitacaoExames()==null && filtro.getCaracteristica()==null && filtro.getTipoColeta()==null){
			throw new ApplicationBusinessException(RelatorioMateriaisColetarRNExceptionCode.MSG_VALIDA_PARAMS_REPORT);
		}
	}
	
	private void verificaExecucaoPlantao(RelatorioMateriaisColetarInternacaoFiltroVO filtro){
		if(verificaDiaPlantao()){
			filtro.setIndExecutaPlantao("S");
		}else{
			filtro.setIndExecutaPlantao(null);
		}
	}

	public boolean verificaDiaPlantao(){
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date());
		
		if(gc.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || gc.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
			return true;
		
		}else{
			AghFeriados feriado = getAghuFacade().obterFeriado(gc.getTime());
			if(feriado !=null){
				if(feriado.getTurno() == null 
						|| (feriado.getTurno().equals(DominioTurno.M) && gc.get(Calendar.HOUR_OF_DAY)  < 12) 
						|| (feriado.getTurno().equals(DominioTurno.T) && gc.get(Calendar.HOUR_OF_DAY) >= 12)
						){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
	}

	private void setaParametrosRelatorio(RelatorioMateriaisColetarInternacaoFiltroVO filtro){
		filtro.setCpParConvenio(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO.toString());
		
		if(filtro.getAelSitItemSolicitacoes() != null && filtro.getAelSitItemSolicitacoes().getCodigo().equalsIgnoreCase("AC")){
			filtro.setCpParColeta(AghuParametrosEnum.P_SITUACAO_A_COLETAR.toString());
			filtro.setpSituacaoAmostra("P");

		}else if(filtro.getAelSitItemSolicitacoes() != null && filtro.getAelSitItemSolicitacoes().getCodigo().equalsIgnoreCase("EC")){
			filtro.setCpParColeta(AghuParametrosEnum.P_SITUACAO_EM_COLETA.toString());
			filtro.setpSituacaoAmostra("E");
		}
	}
	
	private void setaDataPesquisaRelatorio(RelatorioMateriaisColetarInternacaoFiltroVO filtro){
		Calendar calendarData = Calendar.getInstance();  
		calendarData.setTime(filtro.getDtHrExecucao());  

		// calcula data de início  
		calendarData.add(Calendar.DATE,-30);

		filtro.setDataInicialPesquisa(calendarData.getTime());
		filtro.setDataFinalPesquisa(filtro.getDtHrExecucao());
	}
	

	//------------------------------
	//GETTERS / SETTERS
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO(){
		return aelSolicitacaoExameDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO(){
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO(){
		return aelSitItemSolicitacoesDAO;
	}
	
	
	
	protected ItemSolicitacaoExameRN getItemSolicitacaoExameRN() {
		return itemSolicitacaoExameRN;
	}
	
	protected EtiquetasON getEtiquetasON() {
		return etiquetasON;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}

}