package br.gov.mec.aghu.internacao.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioLocalizacaoPaciente;
import br.gov.mec.aghu.dominio.DominioOrdenacaoProcedenciaPacientes;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ProcedenciaPacientesInternadosVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 * 
 */
@Stateless
public class ProcedenciaPacientesInternadosON extends BaseBusiness {


@EJB
private ProcedenciaPacientesInternadosRN procedenciaPacientesInternadosRN;

private static final Log LOG = LogFactory.getLog(ProcedenciaPacientesInternadosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 401671199963619873L;

	/**
	 * Realiza a pesquisa de procedencia de pacientes internados
	 * @param localizacaoPaciente
	 * @param ordenacaoProcedenciaPacientes
	 * @return
	 */
	@Secure("#{s:hasPermission('relatorio','procedenciaPacientesInternados')}")
	@SuppressWarnings("PMD.NPathComplexity")
	public List<ProcedenciaPacientesInternadosVO> pesquisa(DominioLocalizacaoPaciente localizacaoPaciente, DominioOrdenacaoProcedenciaPacientes ordenacaoProcedenciaPacientes) {
		
				
		// Busca em AGH_PARAMETRO
		AghParametrosVO aghParametroVO = new AghParametrosVO();
		aghParametroVO.setNome("P_CONVENIO_SUS_PADRAO");
		
		try {
			this.getParametroFacade().getAghpParametro(aghParametroVO);
		} catch (ApplicationBusinessException e) {
			this.logError("Erro ao capturar parametro P_CONVENIO_SUS_PADRAO: \n" + e);
		}
		
		Short convSusPadrao = null;
		if (aghParametroVO.getVlrNumerico() != null) {
			convSusPadrao = aghParametroVO.getVlrNumerico().shortValue();
		}
		
		List<Object[]> select = getAghuFacade().pesquisarProcedenciaPaciente(convSusPadrao);
		List<ProcedenciaPacientesInternadosVO> retorno = new ArrayList<ProcedenciaPacientesInternadosVO>();
				
		// Popula a primeira parte do VO.
		Iterator<Object[]> it = select.iterator();
		while (it.hasNext()) {
			Object[] obj = it.next();
			Integer pacCodigo = (Integer) obj[0];
			Short unfSeq = (Short) obj[8];
			
			if (!restricao1(localizacaoPaciente, unfSeq)) {
				continue;
			}
			
			if (!restricao2(unfSeq)) {
				continue;
			}
			
			ProcedenciaPacientesInternadosVO vo = new ProcedenciaPacientesInternadosVO();
			
			String cidade = getProcedenciaPacientesInternadosRN().recuperarCidadePaciente(pacCodigo);
			vo.setCidade(cidade);
			
			if (obj[1] != null) {
				String prontAux = ((Integer) obj[1]).toString();
				prontAux = StringUtils.leftPad(prontAux, 7, "0");
				vo.setProntuario(prontAux.substring(0, prontAux.length() - 1)+ "/" + prontAux.charAt(prontAux.length() - 1));
			}
			
			if (obj[2] != null) {
				vo.setNomePaciente((String) obj[2]);
			}
			
			if (obj[3] != null) {
				vo.setLeito((String) obj[3]);
			} else if (obj[4] != null) {
				vo.setLeito((String) obj[4]);
			} else {
				vo.setLeito("");
			}
			
			String andarAlaDescricao = (obj[5] != null ? StringUtils.leftPad(( obj[5]).toString(), 2, "0") : "")
					+ " "
					+ (obj[6] != null ? ((AghAla) obj[6]).toString() : "")
					+ " - "
					+ (obj[7] != null ? (String) obj[7] : "");
			vo.setUnidade(andarAlaDescricao);
						
			if (obj[9] != null) {
				vo.setEspecialidade((String) obj[9]);
			}
			
			if (obj[10] != null) {
				vo.setEquipe((String) obj[10]);
			}
			
			if (obj[11] != null) {
				vo.setNome((String) obj[11]);
			}
			
			if (obj[12] != null) {
				Date dataInicio = (Date) obj[12];
				Long tempo = new Date().getTime() - dataInicio.getTime();
				vo.setTempo((tempo/(24*60*60*1000)));
			}
			
			retorno.add(vo);
		}
		
		ordenarLista(retorno, ordenacaoProcedenciaPacientes);
		
		return retorno;
	}
	
	/**
	 * Restrição  decode(:p_local, 'Emergência',aghc_ver_caract_unf (atd.unf_seq,'Atend emerg terreo'),'Internação',decode(aghc_ver_caract_unf (atd.unf_seq,'Atend emerg terreo'),'N'
	 * @param localizacaoPaciente
	 * @param unfSeq
	 * @return
	 */
	private boolean restricao1(DominioLocalizacaoPaciente localizacaoPaciente, Short unfSeq) {
		
		if (localizacaoPaciente == DominioLocalizacaoPaciente.E && getPesquisaInternacaoFacade().verificarCaracteristicaDaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO) == DominioSimNao.N) {
			return false;
		} else if (localizacaoPaciente == DominioLocalizacaoPaciente.I && getPesquisaInternacaoFacade().verificarCaracteristicaDaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO) == DominioSimNao.S) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Restrição aghc_ver_caract_unf (atd.unf_seq,'CO') <> 'S' and  aghc_ver_caract_unf (atd.unf_seq,'Unid Hosp Dia') <> 'S'
	 * @param unfSeq
	 * @return
	 */
	private boolean restricao2(Short unfSeq) {
		
		if (getPesquisaInternacaoFacade().verificarCaracteristicaDaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.CO) == DominioSimNao.S || getPesquisaInternacaoFacade().verificarCaracteristicaDaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA) == DominioSimNao.S) {
			return false;
		}
		return true;
	}
	
	/**
	 * Realiza a ordenação da lista de acordo com o parâmetro passado pelo usuário
	 * @param lista
	 * @param ordenacaoProcedenciaPacientes
	 */
	private void ordenarLista(List<ProcedenciaPacientesInternadosVO> lista, DominioOrdenacaoProcedenciaPacientes ordenacaoProcedenciaPacientes) {
		
		if (ordenacaoProcedenciaPacientes == DominioOrdenacaoProcedenciaPacientes.C) {
			Collections.sort(lista, new Comparator<ProcedenciaPacientesInternadosVO>() {
				@Override
				public int compare(ProcedenciaPacientesInternadosVO o1, ProcedenciaPacientesInternadosVO o2) {
					return (o1.getCidade() + o1.getUnidade() + o1.getNome() + o1.getNomePaciente()).compareTo((o2.getCidade() + o2.getUnidade() + o2.getNome() + o2.getNomePaciente()));
				}
			});
		} else if (ordenacaoProcedenciaPacientes == DominioOrdenacaoProcedenciaPacientes.E) {
			Collections.sort(lista, new Comparator<ProcedenciaPacientesInternadosVO>() {
				@Override
				public int compare(ProcedenciaPacientesInternadosVO o1, ProcedenciaPacientesInternadosVO o2) {
					return (o1.getNome() + o1.getUnidade() + o1.getCidade() + o1.getNomePaciente()).compareTo((o2.getNome() + o2.getUnidade() + o2.getCidade() + o2.getNomePaciente()));
				}
			});
		} else {
			Collections.sort(lista, new Comparator<ProcedenciaPacientesInternadosVO>() {
				@Override
				public int compare(ProcedenciaPacientesInternadosVO o1, ProcedenciaPacientesInternadosVO o2) {
					return (o1.getUnidade() + o1.getNome() + o1.getCidade() + o1.getNomePaciente()).compareTo((o2.getUnidade() + o2.getNome() + o2.getCidade() + o2.getNomePaciente()));
				}
			});
		}
	}
	
	protected ProcedenciaPacientesInternadosRN getProcedenciaPacientesInternadosRN() {
		return procedenciaPacientesInternadosRN; 
	}
	
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade(){
		return pesquisaInternacaoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

}
