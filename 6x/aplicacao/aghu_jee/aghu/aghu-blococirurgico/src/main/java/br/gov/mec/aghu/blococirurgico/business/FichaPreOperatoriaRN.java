package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class FichaPreOperatoriaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(FichaPreOperatoriaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	@EJB
	private IAghuFacade iAghuFacade;

	private static final long serialVersionUID = 2511612370975623391L;
	
	/**
	 * @ORADB MBCC_IDADE_EXT
	 * @param dtNascimento
	 * @return
	 */
	public String mbccIdadeExt(Date dtNascimento, Date dataCrg){
		String idadeFormat;
		if (dtNascimento != null && dataCrg != null) {
			int anos  = DateUtil.obterQtdAnosEntreDuasDatas(dtNascimento, dataCrg);
			Calendar qtdMesesCalendar = Calendar.getInstance();
			qtdMesesCalendar.setTime(dtNascimento);
			qtdMesesCalendar.add(Calendar.YEAR, anos);
			int meses = DateUtil.obterQtdMesesEntreDuasDatas(qtdMesesCalendar.getTime(), dataCrg);
			idadeFormat = anos + " A " + meses + " M";
		}else{
			idadeFormat = null;
		}
		return idadeFormat;
	}
	
	/**
	 * @ORADB MBCC_GET_UNF_DESC
	 * Antes de utilizar este método verificar se AghUnidadesFuncionais.getLPADAndarAla atende
	 * @return
	 */
	public String mbccGetunfDesc(Short unfSeq){
		AghUnidadesFuncionais unf = unfSeq != null ? getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq) : null;
		if(unf != null){
			return (StringUtils.leftPad(unf.getAndar()
					.toString(), 2, "0"))
					+ " - "
					+ (AghAla.N.equals(unf.getIndAla()) ? "Norte" : "Sul");
		}else{
			return "_______";
		}
	}
	
	/**
	 * @ORADB MBCC_GET_TURNO
	 * @return
	 */
	@SuppressWarnings("ucd")
	public String mbccGetTurno(Date dtHrInicioCirg){
		Integer horarioInteger = Integer.valueOf(DateUtil.dataToString(dtHrInicioCirg, "HHmm"));
		DominioTurno retorno;
		if(horarioInteger > 1029 && horarioInteger < 1431){
			retorno = DominioTurno.M;
		}else{
			if(horarioInteger > 1430 && horarioInteger < 1930){
				retorno = DominioTurno.T;
			}else{
				retorno = DominioTurno.N;
			}
		}
		return retorno.getDescricao().toUpperCase();
	}
	/**
	 * @ORADB MBCC_GET_HH_TRIC
	 * @param dtHrInicioCirg
	 * @return
	 */
	@SuppressWarnings("ucd")
	public Integer mbccGetHhTric(Date dtHrInicioCirg){
		Integer horarioInteger = Integer.valueOf(DateUtil.dataToString(dtHrInicioCirg, "HH"));
		if(horarioInteger < 2){
			return 24 - (2 - horarioInteger);
		}else{
			return horarioInteger - 2;
		}
	}
	
	public IAghuFacade getAghuFacade(){
		return iAghuFacade;
	}
	
}
