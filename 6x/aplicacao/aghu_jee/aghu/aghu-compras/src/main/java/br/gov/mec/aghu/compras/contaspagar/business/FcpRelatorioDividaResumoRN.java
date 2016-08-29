package br.gov.mec.aghu.compras.contaspagar.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.vo.RelatorioDividaResumoVO;
import br.gov.mec.aghu.dominio.DominioMes;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class FcpRelatorioDividaResumoRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8688763550082730213L;

	private static final Log LOG = LogFactory.getLog(FcpRelatorioDividaResumoRN.class);
	
	@Inject
	private FcpTituloDAO fcpTituloDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * Método responsável por pesquisar divida resumo do grupo atrasado.
	 * @param dataFinal
	 * @return
	 * @throws BaseException
	 */
	public List<RelatorioDividaResumoVO> pesquisarGrupoAtrasado(Date dataFinal) throws BaseException {
		List<FcpTitulo> listaConsulta = this.fcpTituloDAO.pesquisarDividaResumoGrupo(dataFinal, true);
		List<RelatorioDividaResumoVO> listaAtrasado = recuperarRelatorioDividaResumo(listaConsulta, true);
		
		return listaAtrasado;
	}
	
	/**
	 * Método responsável por pesquisar divida resumo do grupo a vencer.
	 * @param dataFinal
	 * @return
	 * @throws BaseException
	 */
	public List<RelatorioDividaResumoVO> pesquisarGrupoAVencer(Date dataFinal) throws BaseException {
		List<FcpTitulo> listaConsulta = this.fcpTituloDAO.pesquisarDividaResumoGrupo(dataFinal, false);
		List<RelatorioDividaResumoVO> listaAVencer = recuperarRelatorioDividaResumo(listaConsulta, false);
		
		return listaAVencer;
	}

	/**
	 * Método responsável por preparar as datas vencidas para o relatório.
	 * @param listaConsulta
	 * @return
	 */
	private List<RelatorioDividaResumoVO> recuperarRelatorioDividaResumo(List<FcpTitulo> listaConsulta, Boolean atrasado) {
		List<RelatorioDividaResumoVO> listaRelatorio = new ArrayList<RelatorioDividaResumoVO>();
		if(listaConsulta != null && !listaConsulta.isEmpty()) {
			for (int i = 0; i < listaConsulta.size(); i++) {
				FcpTitulo titulo = (FcpTitulo)listaConsulta.get(i);
				Date dataVencimento = titulo.getDtVencimento();
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dataVencimento);
				Integer mes = calendar.get(Calendar.MONTH);
				Integer ano = calendar.get(Calendar.YEAR);

				String mesFomatado = DominioMes.obterDominioMes(mes).getDescricaoAbreviacao();
				String anoFormatado = String.valueOf(ano).substring(2);				
				String dataVencimentoFormatado = mesFomatado + "/" + anoFormatado;
				
				RelatorioDividaResumoVO vo = new RelatorioDividaResumoVO();
				vo.setAtrasado(atrasado);
				vo.setData(dataVencimentoFormatado);
				vo.setValor(listaConsulta.get(i).getValor());
				
				listaRelatorio.add(vo);
			}
		}
		return listaRelatorio;
	}

	/**
	 * @return the fcpTituloDAO
	 */
	public FcpTituloDAO getFcpTituloDAO() {
		return fcpTituloDAO;
	}

	/**
	 * @param fcpTituloDAO the fcpTituloDAO to set
	 */
	public void setFcpTituloDAO(FcpTituloDAO fcpTituloDAO) {
		this.fcpTituloDAO = fcpTituloDAO;
	}

}
