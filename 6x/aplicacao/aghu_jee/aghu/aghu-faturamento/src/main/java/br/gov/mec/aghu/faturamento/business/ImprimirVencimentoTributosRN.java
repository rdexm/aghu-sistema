package br.gov.mec.aghu.faturamento.business;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.FcpRetencaoTributoDAO;
import br.gov.mec.aghu.compras.vo.FcpCalendarioVencimentoTributosVO;
import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.faturamento.vo.ImprimirVencimentoTributosVO;
import br.gov.mec.aghu.faturamento.vo.TributosVO;
import br.gov.mec.aghu.model.FcpRetencaoTributo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ImprimirVencimentoTributosRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8819518903346077958L;
	private static final Log LOG = LogFactory
			.getLog(ImprimirVencimentoTributosRN.class);

	@Inject
	private FcpRetencaoTributoDAO fcpRetencaoTributoDAO;
	
	@EJB
	private IComprasFacade comprasFacade;

	/**
	 * Recuper tributos vencidos
	 * @param tipoTributo
	 * @return
	 * @throws BaseException 
	 */
	public ImprimirVencimentoTributosVO recuperaTributosVencidos( DominioTipoTributo tipoTributo, Date dataApuracao) throws BaseException {
		List<FcpRetencaoTributo> listaRetencaoTributos = getFcpRetencaoTributoDAO().listaRetencaoTributoPorTipoTributo(tipoTributo);
		List<FcpCalendarioVencimentoTributosVO> fcpCalendariosVencimentosTributosVOs = comprasFacade.listarCalendariosVencimentos(dataApuracao, tipoTributo);
		ImprimirVencimentoTributosVO imprimirVencimentoTributos = new ImprimirVencimentoTributosVO();
		imprimirVencimentoTributos.setMesAno(mesAnoAtual());
		
		
		List<TributosVO> listaTributosFederal = new LinkedList<TributosVO>();
		List<TributosVO> listaTributosPrevidenciarios = new LinkedList<TributosVO>();
		List<TributosVO> listaTributosMunicipal = new LinkedList<TributosVO>();
		for (FcpRetencaoTributo retencaoTributo : listaRetencaoTributos) {
			TributosVO vo = new TributosVO();
			vo.setCodigoRecolhimento(retencaoTributo.getCodigo());
			if (retencaoTributo.getTipoTributo() != null) {
				if (retencaoTributo.getTipoTributo().getSigla() == DominioTipoTributo.F.getSigla()) {
					listaTributosFederal.add(vo);
				} else if (retencaoTributo.getTipoTributo().getSigla() == DominioTipoTributo.P.getSigla()) {
					listaTributosPrevidenciarios.add(vo);
				} else if (retencaoTributo.getTipoTributo().getSigla() == DominioTipoTributo.M.getSigla()) {
					listaTributosMunicipal.add(vo);

				}
			}
		}
		for (FcpCalendarioVencimentoTributosVO fcpCalendarioVencimentoTributosVO : fcpCalendariosVencimentosTributosVOs) {
			TributosVO vo = new TributosVO();
			vo.setCodigoRecolhimento(fcpCalendarioVencimentoTributosVO.getSeq());
			vo.setPeriodoApuracao(fcpCalendarioVencimentoTributosVO.getFormattedFields().getPeriodoApuracao());
			vo.setDataVencimento(fcpCalendarioVencimentoTributosVO.getFormattedFields().getVencimento());
			
			if (fcpCalendarioVencimentoTributosVO.getTipoTributo() != null) {
				if (fcpCalendarioVencimentoTributosVO.getTipoTributo().getSigla() == DominioTipoTributo.F.getSigla()) {
					if (!substituicao(listaTributosFederal, vo)) {
						listaTributosFederal.add(vo);
					}
				} else if (fcpCalendarioVencimentoTributosVO.getTipoTributo().getSigla() == DominioTipoTributo.P.getSigla()) {
					if (!substituicao(listaTributosPrevidenciarios, vo)) {
						listaTributosPrevidenciarios.add(vo);
					}
				} else if (fcpCalendarioVencimentoTributosVO.getTipoTributo().getSigla() == DominioTipoTributo.M.getSigla()) {
					if (!substituicao(listaTributosMunicipal, vo)) {
						listaTributosMunicipal.add(vo);
					}

				}
			}
		}
		imprimirVencimentoTributos.setTributosFederal(listaTributosFederal);
		imprimirVencimentoTributos.setTributosMunicipal(listaTributosMunicipal);
		imprimirVencimentoTributos.setTributosPrevidenciarios(listaTributosPrevidenciarios);
		return imprimirVencimentoTributos;
	}
	
	
	private boolean substituicao(List<TributosVO> lista, TributosVO vo){
		if (lista != null && vo != null) {
			if (lista.contains(vo)) {
				vo.setDataVencimento(vo.getDataVencimento());
				vo.setPeriodoApuracao(vo.getPeriodoApuracao());
				return true; 
			} 
		}
		return false;
		
	}
	
	public String mesAnoAtual(){
		Calendar cal = Calendar.getInstance();
		return 	nomeDoMes(cal.get(Calendar.MONTH)) + " / "+ cal.get(Calendar.YEAR);
	}
	
	
	private static String nomeDoMes(int i) { 
		String mes[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
			return(mes[i]);
	}
	

	protected FcpRetencaoTributoDAO getFcpRetencaoTributoDAO() {
		return this.fcpRetencaoTributoDAO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}