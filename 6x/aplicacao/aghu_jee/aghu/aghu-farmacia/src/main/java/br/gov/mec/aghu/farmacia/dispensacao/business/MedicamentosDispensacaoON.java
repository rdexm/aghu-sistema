package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndExcluidoDispMdtoCbSps;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispMdtoCbSpsDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaPrescricaoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.vo.ConsultaDispensacaoMedicamentosVO;
import br.gov.mec.aghu.farmacia.vo.DispensacaoMedicamentosVO;
import br.gov.mec.aghu.model.AfaDispMdtoCbSps;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaPrescricaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.VMpmMdtosDescr;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class MedicamentosDispensacaoON extends BaseBusiness {

	
	private static final String _EM_ = " em ";

	@EJB
	private RealizarTriagemMedicamentosPrescricaoRN realizarTriagemMedicamentosPrescricaoRN;
	
	private static final Log LOG = LogFactory.getLog(MedicamentosDispensacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;

	@Inject
	private AfaPrescricaoMedicamentoDAO afaPrescricaoMedicamentoDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private AfaDispMdtoCbSpsDAO afaDispMdtoCbSpsDAO;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8683116062211045214L;

	@SuppressWarnings("PMD.NPathComplexity")
	public List<DispensacaoMedicamentosVO> montarListaDispensacaoMedicamentoVO(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			MpmPrescricaoMedica prescricaoMedica, Short unfSeq, Long seqPrescricaoNaoEletronica){
		List<AfaDispensacaoMdtos> listaDispensacao;
		if(seqPrescricaoNaoEletronica != null){
			listaDispensacao = getAfaDispensacaoMdtosDAO().pesquisarDispensacaoMdtosseqPrescricaoNaoEletronica(firstResult,
				maxResults, orderProperty, asc, seqPrescricaoNaoEletronica);
		}else{		
			listaDispensacao = getAfaDispensacaoMdtosDAO().pesquisarDispensacaoMdtosPorPrescricao(firstResult,
				maxResults, orderProperty, asc, prescricaoMedica, unfSeq);
		}
		
		List<DispensacaoMedicamentosVO> listaVOs = new ArrayList<DispensacaoMedicamentosVO>();
		for (AfaDispensacaoMdtos medicamentoDispensacao: listaDispensacao){
			DispensacaoMedicamentosVO vo = new DispensacaoMedicamentosVO();
			
			VMpmMdtosDescr viewMdtoDescr = getPrescricaoMedicaFacade().obterVMpmMdtosDescrPorMedicamento(medicamentoDispensacao.getMedicamento());
			
			if (viewMdtoDescr != null){
				vo.setDescricaoEdit(viewMdtoDescr.getDescricaoEdit());
				vo.setTprSigla(viewMdtoDescr.getTprSigla());
				if(viewMdtoDescr.getTprSigla() != null){
					AfaTipoApresentacaoMedicamento tipoApresentacao = getFarmaciaFacade().obterAfaTipoApresentacaoMedicamentoPorId(viewMdtoDescr.getTprSigla());
					vo.setDescricaoSigla(tipoApresentacao.getDescricao());
				}
				if(medicamentoDispensacao.getPrescricaoMedica() != null){
					vo.setDescricao(getRealizarTriagemMedicamentosPrescricaoRN().obterDescricaoMedicamentoPrescrito(medicamentoDispensacao));
				}else{
					if(medicamentoDispensacao.getObservacao() != null && !medicamentoDispensacao.getObservacao().isEmpty()){
						vo.setDescricao(vo.getDescricaoEdit() + "; " + medicamentoDispensacao.getObservacao());
					}else{
						vo.setDescricao(vo.getDescricaoEdit());
					}
				}
			}
			
			vo.setQtdeSolicitadaDispensacao(medicamentoDispensacao.getQtdeSolicitada());
			vo.setQtdeDispensadaDispensacao(medicamentoDispensacao.getQtdeDispensada());
			
			if(medicamentoDispensacao.getTipoOcorrenciaDispensacao() != null){
				vo.setDescricaoTipoOcorrencia(medicamentoDispensacao.getTipoOcorrenciaDispensacao().getDescricao());
				vo.setSeqTipoOcorrencia(medicamentoDispensacao.getTipoOcorrenciaDispensacao().getSeq());
			}
			vo.setMatCodigo(medicamentoDispensacao.getMedicamento().getMatCodigo());
			vo.setSeqUnidadeFuncional(medicamentoDispensacao.getUnidadeFuncional().getSeq());
			vo.setDescricaoUnidadeFuncional(medicamentoDispensacao.getUnidadeFuncional().getDescricao());
			vo.setSituacaoDispensacao(medicamentoDispensacao.getIndSituacao().getDescricao());
			
			
			//Seta os hints
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			if(medicamentoDispensacao.getServidorTriadoPor() != null){
				vo.setNomeTriadoPor(medicamentoDispensacao.getServidorTriadoPor().getPessoaFisica().getNome());				
			}
			if (medicamentoDispensacao.getDthrTriado() != null){
				vo.setDataTriadoPor(_EM_ + df.format(medicamentoDispensacao.getDthrTriado()));				
			}
			
			if (medicamentoDispensacao.getServidor() != null){
				vo.setNomeDispensadoPor(medicamentoDispensacao.getServidor().getPessoaFisica().getNome());				
			}
			if (medicamentoDispensacao.getDthrDispensacao() != null){
				vo.setDataDispensadoPor(_EM_ + df.format(medicamentoDispensacao.getDthrDispensacao()));
			}
			
			if (medicamentoDispensacao.getServidorConferida() != null){
				vo.setNomeConferidoPor(medicamentoDispensacao.getServidorConferida().getPessoaFisica().getNome());				
			}
			if (medicamentoDispensacao.getDthrConferencia() != null){
				vo.setDataConferidoPor(_EM_ + df.format(medicamentoDispensacao.getDthrConferencia()));
			}
			
			if (medicamentoDispensacao.getServidorEntregue() != null){
				vo.setNomeEntreguePor(medicamentoDispensacao.getServidorEntregue().getPessoaFisica().getNome());				
			}
			if (medicamentoDispensacao.getDthrEntrega() != null){
				vo.setDataEntreguePor(_EM_ + df.format(medicamentoDispensacao.getDthrEntrega()));
			}
			
			
			listaVOs.add(vo);
		}
		
		return listaVOs;
		
	}
	
	private IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	private IFarmaciaFacade getFarmaciaFacade(){
		return this.farmaciaFacade;
	}
	
	protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}

//  Estória #5387
	public List<AfaDispensacaoMdtos>  pesquisarItensDispensadosPorFiltro(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc,AghUnidadesFuncionais unidadeSolicitante, Integer prontuario, String nomePaciente, Date dtHrInclusaoItem, AfaMedicamento medicamento, 
			DominioSituacaoDispensacaoMdto situacao, AghUnidadesFuncionais farmacia, AghAtendimentos aghAtendimentos, String etiqueta, MpmPrescricaoMedica prescricaoMedica, String loteCodigo,
			Boolean indPmNaoEletronica) {		
		
		List<AfaDispensacaoMdtos> listaDispensacao; 
		
		if (etiqueta!=null && !etiqueta.isEmpty()){
			/* Busca a seq da tabela afa_dispensacao_mdtos */
			AfaDispensacaoMdtos dispensacaoMdtos = null;
			AfaDispMdtoCbSps afaDispMdtoCbSps = getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsByEtiqueta(etiqueta,DominioIndExcluidoDispMdtoCbSps.I, loteCodigo);
			if (afaDispMdtoCbSps == null){
				return new ArrayList<AfaDispensacaoMdtos>();
			}
			dispensacaoMdtos = afaDispMdtoCbSps.getDispensacaoMdto();			 
			listaDispensacao = Arrays.asList(dispensacaoMdtos);
			
		}else{
			listaDispensacao = getAfaDispensacaoMdtosDAO()
		  .pesquisarItensDispensadosPorFiltro(firstResult, maxResult, orderProperty, asc, unidadeSolicitante, prontuario, nomePaciente, dtHrInclusaoItem, medicamento, 
				  situacao, farmacia, aghAtendimentos, prescricaoMedica, loteCodigo, indPmNaoEletronica);
		}

		return listaDispensacao;
	}

	// Estória #5387
	public Long pesquisarItensDispensacaoMdtosCount(
			AghUnidadesFuncionais unidadeSolicitante, Integer prontuario,
			String nomePaciente, Date dtHrInclusaoItem,
			AfaMedicamento medicamento,
			DominioSituacaoDispensacaoMdto situacao,
			AghUnidadesFuncionais farmacia, AghAtendimentos aghAtendimentos,
			String etiqueta, MpmPrescricaoMedica prescricaoMedica, String loteCodigo, Boolean indPmNaoEletronica) {
		
		if (etiqueta != null && !etiqueta.isEmpty()) {

			return getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsByEtiquetaCount(
					etiqueta, DominioIndExcluidoDispMdtoCbSps.I, loteCodigo);

		} else {
			return getAfaDispensacaoMdtosDAO()
					.pesquisarItensDispensacaoMdtosCount(unidadeSolicitante,
							prontuario, nomePaciente, dtHrInclusaoItem,
							medicamento, situacao, farmacia, aghAtendimentos, prescricaoMedica, loteCodigo, indPmNaoEletronica);
		}

	}


	protected AfaDispMdtoCbSpsDAO getAfaDispMdtoCbSpsDAO() {
		return afaDispMdtoCbSpsDAO;
	}
	
	private RealizarTriagemMedicamentosPrescricaoRN getRealizarTriagemMedicamentosPrescricaoRN(){
		return realizarTriagemMedicamentosPrescricaoRN;
	}

	public Long pesquisarDispensacaoMdtosCount(MpmPrescricaoMedica prescricaoMedica, Short unfSeq,
			Long seqPrescricaoNaoEletronica) {
		Long retorno;
		if(seqPrescricaoNaoEletronica != null){
			retorno = afaDispensacaoMdtosDAO.pesquisarDispensacaoMdtosseqPrescricaoNaoEletronicaCount(seqPrescricaoNaoEletronica);
		}else{
			retorno = afaDispensacaoMdtosDAO.pesquisarDispensacaoMdtosCount(prescricaoMedica, unfSeq);
		}
		return retorno;
	}

	public ConsultaDispensacaoMedicamentosVO pesquisarDispensacaoMdtosCount(
			MpmPrescricaoMedica prescricaoMedica, Long seqPrescricaoNaoEletronica) {
		ConsultaDispensacaoMedicamentosVO retorno = new ConsultaDispensacaoMedicamentosVO();
		if(prescricaoMedica != null){
			if(prescricaoMedica.getAtendimento().getLeito() != null){
				retorno.setLeito(prescricaoMedica.getAtendimento().getLeito().getLeitoID());
			}
			retorno.setProntuario(prescricaoMedica.getAtendimento().getPaciente().getProntuario());
			retorno.setNome(prescricaoMedica.getAtendimento().getPaciente().getNome());
			retorno.setOrigem("Eletrônica");
			retorno.setNumeroPrescricao(prescricaoMedica.getId().getSeq().toString());
			retorno.setDataInicio(prescricaoMedica.getDthrInicio());
			retorno.setDataFim(prescricaoMedica.getDthrFim());
			retorno.setAtdSeq(prescricaoMedica.getAtendimento().getSeq());
		}else{
			AfaPrescricaoMedicamento prescricaoNaoEletronica = afaPrescricaoMedicamentoDAO.obterPorChavePrimaria(seqPrescricaoNaoEletronica);
			if(prescricaoNaoEletronica.getAtendimento().getLeito() != null){
				retorno.setLeito(prescricaoNaoEletronica.getAtendimento().getLeito().getLeitoID());
			}
			retorno.setProntuario(prescricaoNaoEletronica.getAtendimento().getPaciente().getProntuario());
			retorno.setNome(prescricaoNaoEletronica.getAtendimento().getPaciente().getNome());
			retorno.setOrigem("Não Eletrônica");
			retorno.setNumeroPrescricao(prescricaoNaoEletronica.getNumeroExterno());
			retorno.setDataInicio(prescricaoNaoEletronica.getDthrInicio());
			retorno.setDataFim(prescricaoNaoEletronica.getDthrFim());
			retorno.setAtdSeq(prescricaoNaoEletronica.getAtendimento().getSeq());
		}
		return retorno;
	}

}