package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoSinteticoVO;
import br.gov.mec.aghu.farmacia.vo.TipoUsoMedicamentoVO;
import br.gov.mec.aghu.internacao.vo.MedicamentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

//Estória # 5697
@SuppressWarnings("PMD.AtributoEmSeamContextManager")
@Stateless
public class RelatorioMedicamentoSinteticoON extends BaseBusiness implements Serializable{

private static final Log LOG = LogFactory.getLog(RelatorioMedicamentoSinteticoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AfaMedicamentoDAO afaMedicamentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1853033306091592585L;
	MedicamentoSinteticoVO mdtoSintetico = new MedicamentoSinteticoVO();

	public enum RelatorioMedicamentoSinteticoONExceptionCode implements	BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA
	}

	public List<MedicamentoSinteticoVO> obterListaTodosMedicamentos() throws ApplicationBusinessException {

		MedicamentoSinteticoVO medicamentoSinteticoVO = new MedicamentoSinteticoVO();
		List<MedicamentoSinteticoVO> listaMedicamentoSinteticoVO = new ArrayList<MedicamentoSinteticoVO>();

		medicamentoSinteticoVO = this.processaMedicamento();
		medicamentoSinteticoVO = this.processaTipoUsoMedicamento();

		listaMedicamentoSinteticoVO.add(medicamentoSinteticoVO);

		return listaMedicamentoSinteticoVO;

	}

	public MedicamentoSinteticoVO processaMedicamento() throws ApplicationBusinessException{
		List<MedicamentoVO> listaMedicamento = getAfaMedicamentoDAO().pesquisarTodosMedicamentos();

		if(listaMedicamento==null || listaMedicamento.isEmpty()) {
			throw new ApplicationBusinessException(RelatorioMedicamentoSinteticoONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		int i;
		for (i=0; i<listaMedicamento.size();i++){
			MedicamentoVO medicamentoVO = listaMedicamento.get(i); 
			String concentracaoFormatada = formataConcentracao(medicamentoVO.getConcentracao());
			listaMedicamento.get(i).setConcentracaoEditada(concentracaoFormatada);

		}		

		mdtoSintetico.setMedicamentoList(listaMedicamento);	
		return mdtoSintetico;
	}


	public MedicamentoSinteticoVO processaTipoUsoMedicamento() throws ApplicationBusinessException{
		List<TipoUsoMedicamentoVO> listaTipoUsoMedicamento = getAfaMedicamentoDAO().pesquisarTipoUsoMedicamento();

		if(listaTipoUsoMedicamento==null || listaTipoUsoMedicamento.isEmpty()) {
			throw new ApplicationBusinessException(RelatorioMedicamentoSinteticoONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		mdtoSintetico.setTipoUsoMedicamentoList(listaTipoUsoMedicamento);
		return mdtoSintetico;
	}

	public String formataConcentracao(BigDecimal concentracao) {
		Locale locBR = new Locale("pt", "BR");//Brasil 
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
		dfSymbols.setDecimalSeparator(',');
		DecimalFormat format;
		if(concentracao != null)
		{
			format = new DecimalFormat("#,###,###,###,##0.####", dfSymbols);
			return format.format(concentracao);
		}

		return null;
	}


	private AfaMedicamentoDAO getAfaMedicamentoDAO(){
		return afaMedicamentoDAO;
	}

}
