package br.gov.mec.aghu.emergencia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncExternoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;
import br.gov.mec.aghu.emergencia.dao.MamCaractSitEmergDAO;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamTrgEncExternos;
import br.gov.mec.aghu.model.MamTrgEncExternosId;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.service.IPrescricaoMedicaService;
import br.gov.mec.aghu.prescricaomedica.vo.PostoSaude;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class MamTrgEncExternoRN extends BaseBusiness {

	private static final long serialVersionUID = -276588332066080964L;

	@Inject
	private IPrescricaoMedicaService prescricaoMedicaFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private MamTriagensDAO mamTriagensDAO;

	@Inject
	private MamTrgEncExternoDAO mamTrgEncExternoDAO;

	@Inject
	private MamCaractSitEmergDAO mamCaractSitEmergDAO;
	
	@Inject
	private MamSituacaoEmergenciaDAO mamSituacaoEmergenciaDAO;

	@Inject
	private MarcarConsultaEmergenciaRN marcarConsultaEmergenciaRN;
	
	@Inject
	private RapServidoresDAO servidorDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	private Long trgSeq;
	
	private Boolean estaNatriagem;

	private MamTriagens mamTriagem;
	
	//private Boolean indImpresso;
	
	public enum MamTrgEncExternoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL, MENSAGEM_ERRO_OBTER_PARAMETRO;
	}

	public List<PostoSaude> pesquisarUnidadeSaudeExterno(Object objPesquisa)throws ApplicationBusinessException {
		try {
			return prescricaoMedicaFacade.listarMpmPostoSaudePorSeqDescricao(objPesquisa);

		} catch (ServiceException e) {
			throw new ApplicationBusinessException(MamTrgEncExternoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	//RN01 #29858
	public String realizaEncaminhamentoExternoValidandoGravidade(String observacao,	PostoSaude postoSaude, String especialidade) throws ApplicationBusinessException, ServiceException {
		if (mamTriagem.getSituacaoEmergencia().getSeq() != null) {
			if (this.estaNatriagem) {//C4
				return (this.marcarConsultaEmergenciaRN.verificaGravidadeEncaminhamentoPaciente(null, null, mamTriagem) ? DominioSimNao.S.toString() : DominioSimNao.N.toString());
			}
		}
		return DominioSimNao.N.toString();
	}

	public Boolean existeTriagem(Long trgSeq) {
		this.trgSeq = trgSeq;
		this.setMamTriagem(mamTriagensDAO.obterPorChavePrimaria(trgSeq)); // (c3)
		return this.estaNatriagem = mamCaractSitEmergDAO.isExisteSituacaoEmerg(mamTriagem.getSituacaoEmergencia().getSeq(),	DominioCaracteristicaEmergencia.LISTA_TRIAGEM);// (c4)
	}

	public void verificaCodSituacaoEmergenciaDoEncExt(String hostName,Long trgSeq) throws ApplicationBusinessException {//RN09
		List<Short> listaSeq = mamCaractSitEmergDAO.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.ENC_EXTERNO);//C07
		if (!listaSeq.isEmpty()) {
			MamTriagens mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(this.getMamTriagem().getSeq());
			this.getMamTriagem().setSituacaoEmergencia(this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(listaSeq.get(0)));
			this.marcarConsultaEmergenciaRN.atualizarSituacaoTriagem(this.getMamTriagem(), mamTriagemOriginal, hostName);//RN04
		}
	}

	@Override
	protected Log getLogger() {
		return null;
	}

	public void gravarEncaminhamentoExterno(Long trgSeq, String especialidade, PostoSaude unidade, String obs,String hostName, Short seqp) throws ApplicationBusinessException {
		MamTrgEncExternos mamTrgEncExterno = preparaObjetoToPersist(trgSeq,especialidade, unidade, obs, hostName, seqp);
		this.mamTrgEncExternoDAO.persistir(mamTrgEncExterno);
		
		Object parametro = parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_AGHU_CHECKOUT_EMERGENCIA.toString(), "vlrTexto");
						
		String retorno = ((String) parametro).toString();
		if (retorno == null || retorno.isEmpty()){
			 throw new ApplicationBusinessException(MamTrgEncExternoRNExceptionCode.MENSAGEM_ERRO_OBTER_PARAMETRO);
		}else{
			if(retorno.equalsIgnoreCase(DominioSimNao.S.toString())){
				MamTriagens mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(trgSeq);
				this.marcarConsultaEmergenciaRN.atualizarTriagemMvtTriagemPorTipoMovimento(mamTriagemOriginal, mamTriagemOriginal, DominioTipoMovimento.C, hostName);
			}
		}			
	
	}

	private MamTrgEncExternos preparaObjetoToPersist(Long trgSeq,String especialidade, PostoSaude unidade, String obs,String hostName, Short seqp) {
		MamTrgEncExternosId id = new MamTrgEncExternosId();
		id.setTrgSeq(trgSeq);
		id.setSeqp(seqp);

		MamTrgEncExternos mamTrgEncExterno = new MamTrgEncExternos();
		mamTrgEncExterno.setId(id);
		mamTrgEncExterno.setEspecialidade(especialidade);
		if (unidade != null) {
			mamTrgEncExterno.setPssSeq(unidade.getSeq());
			mamTrgEncExterno.setLocalAtendimento(unidade.getDescricao());
		} else {
			mamTrgEncExterno.setPssSeq(null);
			mamTrgEncExterno.setLocalAtendimento("");
		}
		mamTrgEncExterno.setObservacao(obs);
		mamTrgEncExterno.setCriadoEm(new Date());
		mamTrgEncExterno.setMicNome(hostName);
		mamTrgEncExterno.setIndImpresso(false);	
		
		mamTrgEncExterno.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		mamTrgEncExterno.setDthrEstorno(null);
		mamTrgEncExterno.setSerVinCodigoEstornado(null);

		return mamTrgEncExterno;
	}

	public MamTriagens getMamTriagem() {
		return mamTriagem;
	}

	public void setMamTriagem(MamTriagens mamTriagem) {
		this.mamTriagem = mamTriagem;
	}

	public Short obtemUltimoSEQPDoAcolhimento(Long trgSeq) {
		return  mamTrgEncExternoDAO.obterMaxSeqPTriagem(trgSeq);//RN08
		
	}
	public Long getTrgSeq() {
		return trgSeq;
	}
	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}
	public Boolean getEstaNatriagem() {
		return estaNatriagem;
	}
	public void setEstaNatriagem(Boolean estaNatriagem) {
		this.estaNatriagem = estaNatriagem;
	}
	
	

}
