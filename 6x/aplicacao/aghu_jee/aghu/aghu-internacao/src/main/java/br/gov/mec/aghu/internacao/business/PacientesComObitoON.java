package br.gov.mec.aghu.internacao.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.vo.PacientesComObitoVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * 
 * @author lalegre
 * 
 */


@Stateless
public class PacientesComObitoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PacientesComObitoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinInternacaoDAO ainInternacaoDAO;

@EJB
private IPacienteFacade pacienteFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7942910073190388153L;
	private static final SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat dia = new SimpleDateFormat("dd");
	private static final SimpleDateFormat mes = new SimpleDateFormat("MM");
	private static final SimpleDateFormat ano = new SimpleDateFormat("yyyy");

	/**
	 * Realiza a pesquisa de pacientes com obito.
	 * 
	 * @param dtInicialReferencia
	 * @param dtFinalReferencia
	 * @param idadeInicial
	 * @param idadeFinal
	 * @param sexo
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<PacientesComObitoVO> pesquisaPacientesComObito(
			Date dtInicialReferencia, Date dtFinalReferencia,
			Integer idadeInicial, Integer idadeFinal, DominioSexo sexo) {

		List<Object[]> select1 = getAinInternacaoDAO().pesquisaPacientesComObito(dtInicialReferencia, dtFinalReferencia, sexo);
		List<AipPacientes> select2 = getPacienteFacade().pesquisaPacientesPorSexoDataObito(dtInicialReferencia, dtFinalReferencia, sexo);
		
		List<PacientesComObitoVO> retorno = new ArrayList<PacientesComObitoVO>();
		
		// Popula a primeira parte do VO.
		Iterator<Object[]> it = select1.iterator();
		while (it.hasNext()) {
			Object[] obj = it.next();
			PacientesComObitoVO vo = new PacientesComObitoVO();
			if (obj[0] != null) {
				String prontAux = ((Integer) obj[0]).toString();
				prontAux = StringUtils.leftPad(prontAux, 7, "0");
				vo.setProntuario(prontAux.substring(0, prontAux.length() - 1)+ "/" + prontAux.charAt(prontAux.length() - 1));
				vo.setSecao(prontAux.substring(vo.getProntuario().length() - 4, vo.getProntuario().length() - 2));

			}
			if (obj[1] != null) {
				vo.setNomePaciente((String) obj[1]);
			}
			if (obj[2] != null) {
				vo.setSexo(((DominioSexo) obj[2]).getDescricao().substring(0, 1));
			}
			
			Date dtNascimento = (Date) obj[3];
			vo.setDataNascimento(data.format(dtNascimento));
			
			//Calculo da idade
			if (obj[4] != null) {
				Date dtObito = (Date) obj[4];
				vo.setIdade(calcularIdadePaciente(dtObito, dtNascimento));
			} else {
				vo.setIdade(calcularIdadePaciente(new Date(), dtNascimento));
			}
			
			if (obj[5] != null) {
				Date dthrAltaMedica = (Date) obj[5];
				vo.setDataObito(data.format(dthrAltaMedica));
			}
			
			if (obj[6] != null) {
				vo.setEspecialidade((String) obj[6]);
			}
			
			retorno.add(vo);
		}
		
		// Popula a segunda parte do VO
		for (AipPacientes paciente : select2) {
			PacientesComObitoVO vo = new PacientesComObitoVO();
			if (paciente.getProntuario() != null) {
				String prontAux = paciente.getProntuario().toString();
				prontAux = StringUtils.leftPad(prontAux, 7, "0");
				vo.setProntuario(prontAux.substring(0, prontAux.length() - 1)+ "/" + prontAux.charAt(prontAux.length() - 1));
				vo.setSecao(prontAux.substring(vo.getProntuario().length() - 4, vo.getProntuario().length() - 2));
			}
			vo.setNomePaciente(paciente.getNome());
			if (paciente.getSexo() != null) {
				vo.setSexo(paciente.getSexo().getDescricao().substring(0, 1));
			} else {
				vo.setSexo("I");
			}
			vo.setDataNascimento(data.format(paciente.getDtNascimento()));
			if (paciente.getDtObito() != null) {
				vo.setIdade(calcularIdadePaciente(paciente.getDtObito(), paciente.getDtNascimento()));
			} else {
				vo.setIdade(calcularIdadePaciente(new Date(), paciente.getDtNascimento()));
			}
			if (paciente.getDtObitoExterno() != null) {
				vo.setDataObito(data.format(paciente.getDtObitoExterno()));
			}
			vo.setEspecialidade("");
			retorno.add(vo);
		}
		
		//Restrição por idade
		if (idadeInicial != null || idadeFinal != null) {
			List<PacientesComObitoVO> retornoFiltrado = new ArrayList<PacientesComObitoVO>();
			for (PacientesComObitoVO vo : retorno) {
				if (idadeInicial != null && idadeFinal != null && vo.getIdade() >= idadeInicial && vo.getIdade() <= idadeFinal) {
					retornoFiltrado.add(vo);
				} else if (idadeInicial != null && idadeFinal == null && vo.getIdade() >= idadeInicial) {
					retornoFiltrado.add(vo);
				} else if (idadeInicial == null && idadeFinal != null && vo.getIdade() <= idadeFinal) {
					retornoFiltrado.add(vo);
				}	
			}
			// Ordenação por seção do prontuário.
			// substr(to_char(PAC.PRONTUARIO,'09999999'),7,2)
			// Ex: Para o prontuário 123401/5 o que deve ser considerado para
			// ordenação é o número 01.
			Collections.sort(retornoFiltrado, new PacientesComObitoComparator());
			return retornoFiltrado;
		}
		// Ordenação por seção do prontuário.
		// substr(to_char(PAC.PRONTUARIO,'09999999'),7,2)
		// Ex: Para o prontuário 123401/5 o que deve ser considerado para
		// ordenação é o número 01.
		Collections.sort(retorno, new PacientesComObitoComparator());
		return retorno;
	}
	
	/**
	 * Calcula idade do paciente
	 * @param dtObito
	 * @param dtNascimento
	 * @return
	 */
	private Integer calcularIdadePaciente(Date dtObito, Date dtNascimento) {
		
		Integer idade = Integer.valueOf(ano.format(dtObito)) - Integer.valueOf(ano.format(dtNascimento));
		if (Integer.valueOf(mes.format(dtObito)) == Integer.valueOf(mes.format(dtNascimento))) {
			if (Integer.valueOf(dia.format(dtObito)) > Integer.valueOf(dia.format(dtNascimento))) {
				return idade;
			} else if (idade > 0){
				return idade - 1;
			} else {
				return 0;
			}
		} else if (Integer.valueOf(mes.format(dtObito)) > Integer.valueOf(mes.format(dtNascimento))){
			return idade;
		} else {
			return idade - 1;
		}
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO(){
		return ainInternacaoDAO;
	}

	protected IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}
	
}

/**
 * Classe comparadora utilizada para ordenar a lista de
 * <code>PacientesComObitoVO</code> pela seção do prontuário, que é
 * definida por dois dígitos no numero do prontuário. Ex: Um prontuário como
 * número 123401/5 possui o número do seção 01, representado pelo 5 e 6 dígito.
 * E os itens tem vir ordenados por esse campo.
 * 
 * @author lalegre
 * 
 */
class PacientesComObitoComparator implements
		Comparator<PacientesComObitoVO> {

	@Override
	public int compare(PacientesComObitoVO o1,
			PacientesComObitoVO o2) {

		// Exemplo: o1 = 388208/3 e o2 = 233208/8
		String vo1Desc = ((PacientesComObitoVO) o1).getProntuario();
		String vo2Desc = ((PacientesComObitoVO) o2).getProntuario();

		// Exemplo: secao1 = 08 e secao2 = 08
		int secao1 = Integer.parseInt(vo1Desc.substring(vo1Desc.length() - 4,
				vo1Desc.length() - 2));

		int secao2 = Integer.parseInt(vo2Desc.substring(vo2Desc.length() - 4,
				vo2Desc.length() - 2));

		int pre1 = 0;
		int pre2 = 0;
		int tam1 = vo1Desc.length();
		int tam2 = vo2Desc.length();
	
		if (tam1 > 7) {
			pre1 = Integer.parseInt(vo1Desc.substring(1, vo1Desc.length() - 2));
		} else {
			pre1 = Integer.parseInt(vo1Desc.substring(0, vo1Desc.length() - 2));
		}
		
		if (tam2 > 7) {
			pre2 = Integer.parseInt(vo2Desc.substring(1, vo2Desc.length() - 2));
		} else {
			pre2 = Integer.parseInt(vo2Desc.substring(0, vo2Desc.length() - 2));
		}
		
		if (secao1 > secao2) {
			return 1;
		} else if (secao1 < secao2) {
			return -1;
		} else {
			if (tam1 < tam2) {
				return 1;
			} else if (tam1 > tam2) {
				return -1;
			} else {
				if (pre1 > pre2) {
					return 1;
				} else if (pre1 < pre2) {
					return -1;
				} else {
					return 0;
				}
			}
				
		}
	}	
}
