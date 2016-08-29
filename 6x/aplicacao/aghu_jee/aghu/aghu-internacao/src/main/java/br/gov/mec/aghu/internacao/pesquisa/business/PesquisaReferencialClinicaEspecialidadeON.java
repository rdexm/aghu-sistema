package br.gov.mec.aghu.internacao.pesquisa.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoIndicador;
import br.gov.mec.aghu.internacao.dao.AinIndicadorHospitalarResumidoDAO;
import br.gov.mec.aghu.internacao.vo.PesquisaReferencialClinicaEspecialidadeVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AinIndicadorHospitalarResumido;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PesquisaReferencialClinicaEspecialidadeON extends BaseBusiness {


@EJB
private PesquisaReferencialRN pesquisaReferencialRN;

private static final Log LOG = LogFactory.getLog(PesquisaReferencialClinicaEspecialidadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinIndicadorHospitalarResumidoDAO ainIndicadorHospitalarResumidoDAO;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2312931928911306015L;

	private enum PesquisaReferencialClinicaEspecialidadeONExceptionCode
			implements BusinessExceptionCode {
		MENSAGEM_INFORMAR_CLINICA;
	}

	public Long pesquisaReferencialClinicaEspecialidadeCount(AghClinicas clinica) throws ApplicationBusinessException {
		this.validaDadosPesquisaReferencialClinicaEspecialidade(clinica);

		Long result = 0l;

		// Count do SQL #1 da view
		result += getAghuFacade().countEspecialidadeReferencial(clinica);

		// Count do SQL #2 da view
		result++;

		// Count do SQL #3 da view
		result++;

		// Count do SQL #4 da view
		result += getAghuFacade().countClinicaReferencial(clinica);

		return result;
	}

	/**
	 * ORADB V_AIN_PES_REF_CLI_ESP
	 * 
	 * Esta view realiza o join de 4 SQLs, os quais serão transcritos como SQLs
	 * independentes e adicionados a mesma coleção, paga unificar os resultados.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param clinica
	 * 
	 * @return
	 */
	@Secure("#{s:hasPermission('referencialClinicaEspecialidade','pesquisar')}")
	public List<PesquisaReferencialClinicaEspecialidadeVO> pesquisaReferencialClinicaEspecialidade(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghClinicas clinica) throws ApplicationBusinessException {
		this.validaDadosPesquisaReferencialClinicaEspecialidade(clinica);

		PesquisaReferencialClinicaEspecialidadeVO _aux = null;

		List<PesquisaReferencialClinicaEspecialidadeVO> resultList = new ArrayList<PesquisaReferencialClinicaEspecialidadeVO>();

		// SQL #1 da view
		resultList.addAll(getAghuFacade().listaEspecialidadeReferencial(clinica));

		// O SQL #2 da view apenas carrega o código da clínica com ordem 2 e
		// sigla 'ADM'.
		_aux = new PesquisaReferencialClinicaEspecialidadeVO(clinica
				.getCodigo(), 0, "ADM", 0, BigDecimal.ZERO, BigDecimal.ZERO);
		resultList.add(_aux);

		// O SQL #3 da view apenas carrega o código da clínica com ordem 3 e
		// sigla 'AST'.
		_aux = new PesquisaReferencialClinicaEspecialidadeVO(clinica
				.getCodigo(), 0, "AST", 0, BigDecimal.ZERO, BigDecimal.ZERO);
		resultList.add(_aux);

		// SQL #4 da view		
		resultList.addAll(getAghuFacade().listaClinicaferencial(clinica));

		// Aplica as regras de negócio migradas da pll
		calculaInformacoesVO(resultList);
		
		// Atribui a média de permanência, taxa de ocupação e média de paciente dia para as especialidades do VO
		this.calcularIndicadoresEspecialidades(resultList, clinica);

		// A paginação é feita de forma manual, eliminando elementos que não
		// devem aparecer no resultado da pesquisa. 10 é o tamanho máximo de
		// itens por página.
		if (resultList.size() > 10 && firstResult < resultList.size()) {
			int toIndex = firstResult + maxResult;

			if (toIndex > resultList.size()) {
				toIndex = resultList.size();
			}

			resultList = resultList.subList(firstResult, toIndex);
		}

		return resultList;
	}
	
	private void calcularIndicadoresEspecialidades(List<PesquisaReferencialClinicaEspecialidadeVO> resultList, AghClinicas clinica) {
		
		// Pesquisa dados de AIN_IND_HOSPITALAR_RESUMIDO
		Date ultimoIndicadorHospitalarGerado = this.getAinIndicadorHospitalarResumidoDAO().obterUltimoIndicadorHospitalarGerado();
		
		// Atribui zero a média de permanência de todos registros do VO
		for (PesquisaReferencialClinicaEspecialidadeVO vo : resultList) {
			vo.setMediaPermanencia(BigDecimal.ZERO);
			vo.setMediaPacienteDia(BigDecimal.ZERO);
		}

		// Testa se foi encontrado um registro com o ultimo indicador.
		// Caso não tenha sido encontrado é necessário executar os indicadores para um período, 
		// pois a tabela AIN_IND_HOSPITALAR_RESUMIDO está vazia. 
		if (ultimoIndicadorHospitalarGerado != null) {
			Calendar cal = Calendar.getInstance();
			// Garante pegar o mes correto mesmo no horário de verão, 
			// já que essa data é usada sometne para ver quantos dias tem o mes
			cal.setTime(ultimoIndicadorHospitalarGerado);
			Integer quantidadeDiasMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			
			List<AinIndicadorHospitalarResumido> indicadorList = this
						.getAinIndicadorHospitalarResumidoDAO()
						.pesquisarIndicadorHospitalarEspecialidadePorClinica(ultimoIndicadorHospitalarGerado,
							DominioTipoIndicador.E, clinica.getCodigo());
			
			// Atribui a "média de permanência", calcula a "média de paciente dia"
			// MEDIA_PAC_DIA = PAC_DIA / QTDE DIAS DO MES
			for (PesquisaReferencialClinicaEspecialidadeVO vo : resultList) {
				for (AinIndicadorHospitalarResumido ainIndicadorHospitalarResumido : indicadorList) {
					if (ainIndicadorHospitalarResumido != null
							&& ainIndicadorHospitalarResumido.getEspecialidade()
									.getSeq().equals(vo.getSeqEspecialidade())) {
						// Seta média de permanência
						vo.setMediaPermanencia(ainIndicadorHospitalarResumido.getMediaPermanencia());
						
						BigDecimal pacienteDia = new BigDecimal(ainIndicadorHospitalarResumido.getQuantidadePaciente());
						BigDecimal diasMes = new BigDecimal(quantidadeDiasMes);
						
						BigDecimal mediaPacienteDia = pacienteDia.divide(diasMes, 2, BigDecimal.ROUND_UP);
						
						// Seta média de paciente dia
						vo.setMediaPacienteDia(mediaPacienteDia);
						break;
					}
				}
			}
		} 
	}
	
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void calculaInformacoesVO(
			List<PesquisaReferencialClinicaEspecialidadeVO> resultList)
			throws ApplicationBusinessException {
		Long gPac = 0l;
		Long gAdm = 0l;
		Long gAst = 0l;
		Long gBloqueio = 0l;
		Long gReferencial = 0l;
		Long gRefAdm = 0l;
		Long gCti = 0l;
		Long gAptos = 0l;
		Long gUnidades = 0l;
		Long gTotal = 0l;

		for (PesquisaReferencialClinicaEspecialidadeVO vo : resultList) {
			if (vo.getSeqEspecialidade() != null) {
				Short seqEspecialidade = vo.getSeqEspecialidade().shortValue();
				Long[] contaInternados = this.getPesquisaReferencialRN()
						.contaInternados(seqEspecialidade);
				Long adm = contaInternados[0];
				Long ast = contaInternados[1];

				Long bloqueios = this.getPesquisaReferencialRN()
						.contaBloqueios(seqEspecialidade);
				vo.setBloqueios(bloqueios);

				vo.setCapacidadeReferencialCalculado(vo
						.getCapacidadeReferencial());
				Long referencial = vo.getCapacidadeReferencialCalculado() != null ? vo
						.getCapacidadeReferencialCalculado().longValue()
						: 0l;

				Long[] contaCti = this.getPesquisaReferencialRN()
						.contaCti(seqEspecialidade);
				Long ctia = contaCti[0];
				Long ctii = contaCti[1];

				vo.setCti(ctia + ctii);

				Long[] contaAptos = this.getPesquisaReferencialRN()
						.contaAptos(seqEspecialidade);
				Long aptosa = contaAptos[0];
				Long aptosi = contaAptos[1];

				vo.setAptos(aptosa + aptosi);

				Long[] contaOutrasUnidades = this.getPesquisaReferencialRN()
						.contaOutrasUnidades(seqEspecialidade);
				Long outrasUnidadesa = contaOutrasUnidades[0];
				Long outrasUnidadesi = contaOutrasUnidades[1];

				vo.setOutrasUnidades(outrasUnidadesa + outrasUnidadesi);

				Long[] contaOutrasClinicas = this.getPesquisaReferencialRN()
						.contaOutrasClinicas(seqEspecialidade);
				Long outrasClinicasa = contaOutrasClinicas[0];
				Long outrasClinicasi = contaOutrasClinicas[1];

				vo.setOutrasClinicas(outrasClinicasa + outrasClinicasi);

				Long total = ast + adm;

				ast = ast - ctia - aptosa - outrasUnidadesa - outrasClinicasa;
				adm = adm - ctii - aptosi - outrasUnidadesi - outrasClinicasi;

				vo.setElet(adm);
				vo.setUrg(ast);

				Long internados = ast + adm;
				vo.setPac(internados);
				vo.setTotal(total);

				vo.setDiferenca(internados - (referencial - bloqueios));

				if ("ADM".equalsIgnoreCase(vo.getSiglaEspecialidade())) {
					gRefAdm = (long) (gReferencial * 0.35);

					vo.setElet(gAdm);
					vo.setDiferenca(gAdm - gRefAdm);
					vo.setCapacidadeReferencialCalculado(gRefAdm);
				} else if ("AST".equalsIgnoreCase(vo.getSiglaEspecialidade())) {
					Long refAst = gReferencial - gRefAdm;

					vo.setUrg(gAst);
					vo.setDiferenca(gAst - refAst);
					vo.setCapacidadeReferencialCalculado(refAst);
				} else if ("TOT".equalsIgnoreCase(vo.getSiglaEspecialidade())) {
					vo.setPac(gPac);
					vo.setElet(gAdm);
					vo.setUrg(gAst);
					vo.setBloqueios(gBloqueio);
					vo.setCapacidadeReferencialCalculado(gReferencial);
					vo.setDiferenca(gPac - gReferencial);
					vo.setCti(gCti);
					vo.setAptos(gAptos);
					vo.setOutrasUnidades(gUnidades);
					vo.setOutrasClinicas(0l);
					vo.setTotal(gTotal);
				} else {
					gAdm += adm;
					gAst += ast;
					gPac += internados;
					gBloqueio += bloqueios;
					gReferencial += referencial;
					gCti += (ctii + ctia);
					gAptos += (aptosi + aptosa);
					gUnidades += (outrasUnidadesi + outrasUnidadesa);
					gTotal += total;
				}
			}
		}
	}

	public void validaDadosPesquisaReferencialClinicaEspecialidade(
			AghClinicas clinica) throws ApplicationBusinessException {
		if (clinica == null) {
			throw new ApplicationBusinessException(
					PesquisaReferencialClinicaEspecialidadeONExceptionCode.MENSAGEM_INFORMAR_CLINICA);
		}
	}

	protected PesquisaReferencialRN getPesquisaReferencialRN() {
		return pesquisaReferencialRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected AinIndicadorHospitalarResumidoDAO getAinIndicadorHospitalarResumidoDAO() {
		return ainIndicadorHospitalarResumidoDAO;
	}
}
