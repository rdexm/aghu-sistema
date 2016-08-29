package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.dominio.DominioValorDataItemSumario;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpeUnidPacAtendimento;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.MpmAltaImpDiagnostica;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO.StatusAltaObito;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.prescricaoenfermagem.vo.RelSumarioPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SumarioPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioPrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class ConsultaInternacaoON extends BaseBusiness {

	private static final String PARA_ = "PARA: ";

	private static final String UNIDADE_ = "Unidade: ";

	@EJB
	private InformacoesPerinataisON informacoesPerinataisON;
	
	private static final Log LOG = LogFactory.getLog(ConsultaInternacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IControlePacienteFacade controlePacienteFacade;
	
	@EJB
	private IPerinatologiaFacade perinatologiaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2654629483294397192L;
	
	private static final String ATENDIMENTO_URGENCIA = "ATU";

	private static final String INTERNACAO = "INT";

	private static final Comparator<InternacaoVO> COMPARATOR_INTERNACOES_VO = new Comparator<InternacaoVO>() {

		@Override
		public int compare(InternacaoVO o1, InternacaoVO o2) {
			int retValue = o2.getDthrInicio().compareTo(o1.getDthrInicio());
			return retValue == 0 ? o2.getDthrFim().compareTo(o1.getDthrFim())
					: retValue;
		}

	};

	public InternacaoVO buscaDetalhes(String tipo, Integer sequence)
			throws ApplicationBusinessException {
		InternacaoVO internacaoVO = null;
		
		// verificar se o HU possui parâmetro para certificação digital
		AghParametros aghParametros = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL);
		
		DominioSimNao possuiCertificacaoEletronica = "S".equals(aghParametros
				.getVlrTexto()) ? DominioSimNao.S : DominioSimNao.N;
		
		if (ATENDIMENTO_URGENCIA.equalsIgnoreCase(tipo)) {
			AinAtendimentosUrgencia atendimentoUrgencia = this.getInternacaoFacade().pesquisaAtendimentoUrgencia(sequence, null);

			if (atendimentoUrgencia != null) {
				internacaoVO = populaInternacaoVO(atendimentoUrgencia);
			}
		} else if (INTERNACAO.equalsIgnoreCase(tipo)) {
			AinInternacao internacao = this.getInternacaoFacade().pesquisaInternacao(sequence, null);

			if (internacao != null) {
				internacaoVO = populaInternacaoVO(internacao, possuiCertificacaoEletronica);
			}
		}

		return internacaoVO;
	}

	
	/**
	 * Realiza a chamada do método para popular o VO e inclui o set do status
	 * alta/óbito deixando inalteradas as outras chamadas da populaInternacaoVO
	 * 
	 * @param internacao
	 * @param possuiCertificacaoEletronica
	 * @return
	 */
	private InternacaoVO populaInternacaoVO(AinInternacao internacao,
			DominioSimNao possuiCertificacaoEletronica) {
		InternacaoVO internacaoVO = populaInternacaoVO(internacao);
		this.atualizarStatusSumarioInternacao(internacao, possuiCertificacaoEletronica, internacaoVO);
		internacaoVO.setFlagExibeSumarioDeTransferencia(getPrescricaoMedicaFacade().verificarSumarioTransfPacInternacao(internacaoVO.getAtdSeq(), null));
		internacaoVO.setPossuiRegistroControlepaciente(this.getInternacaoFacade().habilitarDadosControle(internacaoVO.getCodigoPaciente(), internacaoVO.getAtdSeq()));
		if(StringUtils.isBlank(internacaoVO.getDescricaoCid())){
			List<MpmAltaImpDiagnostica> listaAltaImpDiagnostica = this.getPrescricaoMedicaFacade().pesquisarAltaImpDiagnosticaPorAtendimento(internacaoVO.getAtdSeq());
			if(listaAltaImpDiagnostica!=null && listaAltaImpDiagnostica.size()>0){
				MpmAltaImpDiagnostica altaImpDiagnostica = listaAltaImpDiagnostica.get(0);
				internacaoVO.setCodigoCid(altaImpDiagnostica.getCidCodigo());
				internacaoVO.setDescricaoCid(altaImpDiagnostica.getDescDiagnostico());
			}
		}
		return internacaoVO;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private InternacaoVO populaInternacaoVO(AinInternacao internacao) {
		InternacaoVO internacaoVO = new InternacaoVO();
		internacaoVO.setSeq(internacao.getSeq());
		internacaoVO.setTipo(INTERNACAO);
		internacaoVO.setIndPacAtendimento(internacao.getAtendimento().getIndPacAtendimento());
		internacaoVO.setCodigoPaciente(internacao.getPaciente().getCodigo());
		
		if (internacao.getPaciente() != null && internacao.getPaciente().getIndPacProtegido() != null) {
			internacaoVO.setIndPacProtegido(internacao.getPaciente().getIndPacProtegido());
		}

		internacaoVO.setPossuiDataItemSumarios(!getPrescricaoMedicaFacade()
				.listarDataItemSumario(internacao.getAtendimento().getSeq())
				.isEmpty());
	
		internacaoVO.setPossuiDataItemSumariosPE(!getPrescricaoEnfermagemFacade()
				.listarDataItemSumario(internacao.getAtendimento().getSeq())
				.isEmpty());
		
		if (!this
				.getControlePacienteFacade()
				.listarHorarioControlePorSeqAtendimento(
						internacao.getAtendimento().getSeq()).isEmpty()) {
			internacaoVO.setPossuiRegistroControlepaciente(true);
		}

		// Se existir atendimento de urgencia antes da internação é atribuida a
		// data de atendimento de urgencia a VO.
		if (internacao.getAtendimento().getAtendimentoUrgencia() != null
				&& internacao.getAtendimento().getAtendimentoUrgencia()
						.getDtAtendimento() != null) {
			internacaoVO.setDthrInicio(internacao.getAtendimento()
					.getAtendimentoUrgencia().getDtAtendimento());
		} else {
			internacaoVO.setDthrInicio(internacao.getDthrInternacao());
		}

		internacaoVO.setAtdSeq(internacao.getAtendimento().getSeq());
		internacaoVO.setNomeEspecialidade(internacao.getEspecialidade().getNomeEspecialidade());
		internacaoVO.setConvenio(internacao.getConvenioSaudePlano().getConvenioSaude().getDescricao());
		internacaoVO.setNomeProfessor(internacao.getServidorProfessor().getPessoaFisica().getNome());
		internacaoVO.setServico(internacao.getEspecialidade().getCentroCusto().getDescricao());

		if (internacao.getLeito() != null) {
			internacaoVO.setLeito(internacao.getLeito().getLeitoID());
		} else if (internacao.getQuarto() != null) {
			internacaoVO.setLeito("Quarto: "+ internacao.getQuarto().getDescricao());
		} else if (internacao.getAtendimento().getUnidadeFuncional() != null) {
			if (internacao.getAtendimento().getUnidadeFuncional().getSigla() != null) {
				internacaoVO.setLeito(UNIDADE_+ internacao.getAtendimento().getUnidadeFuncional().getSigla());
			}
			if (internacao.getAtendimento().getUnidadeFuncional().getAndarAlaDescricao() != null) {
				internacaoVO.setLeito(UNIDADE_+ internacao.getAtendimento().getUnidadeFuncional().getAndarAlaDescricao());
			}
		}
		if (internacao.getDthrAltaMedica() != null) {
			internacaoVO.setDthrFim(internacao.getDthrAltaMedica());
		}
		if (internacao.getDtPrevAlta() != null) {
			internacaoVO.setDtPrevisao(internacao.getDtPrevAlta());
		}

		if (internacao.getAtendimento() != null) {
			internacaoVO.setIndPacAtendimento(internacao.getAtendimento().getIndPacAtendimento());
			internacaoVO.setGsoSeqp(internacao.getAtendimento().getGsoSeqp());

			MpmPrescricaoMedica pm = internacao.getAtendimento().getPrescricaoMedicaMenorAtendimento(
							DateUtil.truncaData(internacaoVO.getDthrInicio()));
			if (pm != null && pm.getServidorValida() != null) {
				internacaoVO.setDtReferencia(pm.getDtReferencia());
			}
			
			if (internacao.getAtendimento().getConsulta() != null) {
				internacaoVO.setNumeroConsulta(internacao.getAtendimento().getConsulta().getNumero());
			}
		}

		MpmAltaSumario altaSumario = buscaAltaSumarioConcluido(internacao.getAtendimento());
		if (altaSumario != null && altaSumario.getAltaDiagPrincipal() != null
				&& altaSumario.getAltaDiagPrincipal().getCid() != null) {

			internacaoVO.setCodigoCid(altaSumario.getAltaDiagPrincipal()
					.getCid().getCodigo());
			internacaoVO.setDescricaoCid(altaSumario.getAltaDiagPrincipal()
					.getCid().getDescricao());
		} else {
			AghCid aghCid = new AghCid();
			List<AinCidsInternacao> cidsInternacaos = new ArrayList<AinCidsInternacao>();
			cidsInternacaos.addAll(aghuFacade.pesquisarCidsInternacao(internacao.getSeq()));
			
			if(internacaoVO.getDescricaoCids() == null){
				internacaoVO.setDescricaoCids(new ArrayList<String>());
			}
			
			for (AinCidsInternacao aci : cidsInternacaos) {
				aghCid = getAghuFacade().obterAghCidPorChavePrimaria(aci.getCid().getSeq());
				if (aghCid != null && aghCid.getCid() != null) {
					internacaoVO.setCodigoCid(aghCid.getCid().getCodigo());
					internacaoVO.getDescricaoCids().add(aghCid.getDescricao());
				}
			}
		}
		
		if(internacao.getPaciente() != null){
			internacaoVO.setProntuario(internacao.getPaciente().getProntuario());
		}
		return internacaoVO;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private InternacaoVO populaInternacaoVO(AinAtendimentosUrgencia atendimentoUrgencia) {
		InternacaoVO internacaoVO = new InternacaoVO();
		
		internacaoVO.setAtdSeq(atendimentoUrgencia.getAtendimento().getSeq());
		internacaoVO.setSeq(atendimentoUrgencia.getSeq());
		internacaoVO.setTipo(ATENDIMENTO_URGENCIA);
		internacaoVO.setDthrInicio(atendimentoUrgencia.getDtAtendimento());
		internacaoVO.setConvenio(atendimentoUrgencia.getConvenioSaude().getDescricao());
		internacaoVO.setNomeProfessor(null);
		internacaoVO.setDtPrevisao(null);
		internacaoVO.setCodigoPaciente(atendimentoUrgencia.getPaciente().getCodigo());
		if (atendimentoUrgencia.getDtAltaAtendimento() != null){
			internacaoVO.setDthrFim(atendimentoUrgencia.getDtAltaAtendimento());
		}	
		if (atendimentoUrgencia.getLeito() != null){
			internacaoVO.setLeito(atendimentoUrgencia.getLeito().getLeitoID());			
		}else if (atendimentoUrgencia.getQuarto()!=null){
			internacaoVO.setLeito("Quarto: " + atendimentoUrgencia.getQuarto().getDescricao());
		}else if (atendimentoUrgencia.getUnidadeFuncional() != null ){
			if (atendimentoUrgencia.getUnidadeFuncional().getSigla()!=null){
				internacaoVO.setLeito(UNIDADE_ + atendimentoUrgencia.getUnidadeFuncional().getSigla());
			}	
			if (atendimentoUrgencia.getUnidadeFuncional().getAndarAlaDescricao()!=null){
				internacaoVO.setLeito(UNIDADE_ + atendimentoUrgencia.getUnidadeFuncional().getAndarAlaDescricao());
			}	
		}		
		if (atendimentoUrgencia.getEspecialidade() != null){
			internacaoVO.setNomeEspecialidade(atendimentoUrgencia.getEspecialidade().getNomeEspecialidade());
		}	
		if (atendimentoUrgencia.getEspecialidade() != null){
			internacaoVO.setServico(atendimentoUrgencia.getEspecialidade().getCentroCusto().getDescricao());
		}
		if (atendimentoUrgencia.getAtendimento()!=null){
			internacaoVO.setIndPacAtendimento(atendimentoUrgencia.getAtendimento().getIndPacAtendimento());
			
			MpmPrescricaoMedica pm = atendimentoUrgencia.getAtendimento()
						.getPrescricaoMedicaMenorAtendimento(DateUtil.truncaData(internacaoVO.getDthrInicio()));
			if (pm!=null && pm.getServidorValida()!=null){
				internacaoVO.setDtReferencia(pm.getDtReferencia());
			}	
		}
		MpmAltaSumario altaSumario = buscaAltaSumarioConcluido(atendimentoUrgencia.getAtendimento());
		if (altaSumario != null && altaSumario.getAltaDiagPrincipal() != null
				&& altaSumario.getAltaDiagPrincipal().getCid() != null) {
			internacaoVO.setCodigoCid(altaSumario.getAltaDiagPrincipal().getCid().getCodigo());
			internacaoVO.setDescricaoCid(altaSumario.getAltaDiagPrincipal().getCid().getDescricao());
		} else {
			MpmCidAtendimento cidAtendimento = buscaCidAtendimentoPrincipalAlta(atendimentoUrgencia.getAtendimento());

			if (cidAtendimento != null && cidAtendimento.getCid() != null) {
				internacaoVO.setCodigoCid(cidAtendimento.getCid().getCodigo());
				internacaoVO.setDescricaoCid(cidAtendimento.getCid().getDescricao());
			} 
		}
		if(StringUtils.isBlank(internacaoVO.getDescricaoCid())){
			List<MpmAltaImpDiagnostica> listaAltaImpDiagnostica = this.getPrescricaoMedicaFacade().pesquisarAltaImpDiagnosticaPorAtendimento(atendimentoUrgencia.getAtendimento().getSeq());
			if(listaAltaImpDiagnostica!=null && listaAltaImpDiagnostica.size()>0){
				MpmAltaImpDiagnostica altaImpDiagnostica = listaAltaImpDiagnostica.get(0);
				internacaoVO.setCodigoCid(altaImpDiagnostica.getCidCodigo());
				internacaoVO.setDescricaoCid(altaImpDiagnostica.getDescDiagnostico());
			}
		}
		
		internacaoVO.setPossuiDataItemSumariosPE(!getPrescricaoEnfermagemFacade()
				.listarDataItemSumario(atendimentoUrgencia.getAtendimento().getSeq())
				.isEmpty());
		
		internacaoVO.setIndPacProtegido(atendimentoUrgencia.getPaciente().getIndPacProtegido());
		
		if(atendimentoUrgencia.getPaciente() != null){
			internacaoVO.setProntuario(atendimentoUrgencia.getPaciente().getProntuario());
		}
		return internacaoVO;
	}

	public MpmAltaSumario buscaAltaSumarioConcluido(AghAtendimentos atendimento) {
		return this.getPrescricaoMedicaFacade().pesquisarAltaSumarioConcluido(atendimento.getSeq());
	}

	public MpmCidAtendimento buscaCidAtendimentoPrincipalAlta(
			AghAtendimentos atendimento) {
		return this.getPrescricaoMedicaFacade().pesquisaCidAtendimentoPrincipal(atendimento);
	}

	public Long pesquisaInternacoesCount(Integer numeroProntuario)
			throws ApplicationBusinessException {
		IInternacaoFacade internacaoFacade = this.getInternacaoFacade();
		Long count = 0L;

		count += internacaoFacade.pesquisaAtendimentoUrgenciaCount(null, numeroProntuario);
		count += internacaoFacade.pesquisaInternacaoCount(null, numeroProntuario);

		return count;
	}
	
	public InternacaoVO obterInternacao(Integer seq, String tipo) throws ApplicationBusinessException {
	       
        AghParametros aghParametros = getParametroFacade().buscarAghParametro(
                AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL);

        DominioSimNao possuiCertificacaoEletronica = "S".equals(aghParametros
                .getVlrTexto()) ? DominioSimNao.S : DominioSimNao.N;

        if(INTERNACAO.equals(tipo)){
            AinInternacao internacao = getInternacaoFacade().obterInternacao(seq);
            if (internacao != null) {
                return populaInternacaoVO(internacao, possuiCertificacaoEletronica);
            }
        } else if(ATENDIMENTO_URGENCIA.equals(tipo)){
            AinAtendimentosUrgencia urgencia = getInternacaoFacade().obterAtendimentoUrgencia(seq);
            if (urgencia != null) {
                return populaInternacaoVO(urgencia);
            }
        }

        return null;
    }
	

	public List<InternacaoVO> pesquisaInternacoes(Integer numeroProntuario) throws ApplicationBusinessException {
		List<InternacaoVO> resultList = new ArrayList<InternacaoVO>();
		
		// verificar se o HU possui parâmetro para certificação digital
		AghParametros aghParametros = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL);
		
		DominioSimNao possuiCertificacaoEletronica = "S".equals(aghParametros.getVlrTexto()) ? DominioSimNao.S : DominioSimNao.N;

		List<AinAtendimentosUrgencia> atendimentosUrgencia = this.getInternacaoFacade().listarAtendimentosUrgencia(null, numeroProntuario);

		if (!atendimentosUrgencia.isEmpty()) {
			for (AinAtendimentosUrgencia atendimentoUrgencia : atendimentosUrgencia) {
				resultList.add(populaInternacaoVO(atendimentoUrgencia));
			}
		}

		List<AinInternacao> internacoes = this.getInternacaoFacade().listarInternacoes(null, numeroProntuario);

		if (!internacoes.isEmpty()) {
			for (AinInternacao internacao : internacoes) {
				resultList.add(populaInternacaoVO(internacao,possuiCertificacaoEletronica));
			}
		}

		Collections.sort(resultList, COMPARATOR_INTERNACOES_VO);
		
		/*//Incluído nova iteração quando a quantidade de registros foi limitada a 10
		for(InternacaoVO vo : resultList){
			vo.setFlagExibeSumarioDeTransferencia(getPrescricaoMedicaFacade().verificarSumarioTransfPacInternacao(vo.getAtdSeq(), null));
		}*/
		return resultList;
	}
	
	/*
	 * #4336 - Imprimir sumário da prescrição médica
	 * Modulo: Montagem da Lista Quinzenal de Seleção para 
	 * 		   relatório
	 * Autor: Cristiano de Quadros da Silva
	 * Data: 07/12/2010
	 */	
	public List<SumarioPrescricaoMedicaVO> montaQuinzenaPrescricaoMedica(InternacaoVO internacao, int codPaciente) throws ApplicationBusinessException{
		
		List<SumarioPrescricaoMedicaVO> listQuinzenaPrescricaoMedica = new ArrayList<SumarioPrescricaoMedicaVO>();
		
		Calendar dtInicio = Calendar.getInstance(); 
		dtInicio.setTime(internacao.getDtReferencia()!=null?
				DateUtil.truncaData(internacao.getDtReferencia()):DateUtil.truncaData(internacao.getDthrInicio()));
		
		Calendar dtFim = Calendar.getInstance();
		if (internacao.getDthrFim()!=null){
			dtFim.setTime(DateUtil.truncaData(internacao.getDthrFim()));
		}		
		Calendar dtAux = Calendar.getInstance();
		Integer index = 0;
		while(!DateUtil.truncaData(dtInicio.getTime()).after(DateUtil.truncaData(dtFim.getTime()))) {
			
			dtAux.setTime(dtInicio.getTime());			
			dtAux.add(Calendar.DATE, 14);
			
			SumarioPrescricaoMedicaVO vo = new SumarioPrescricaoMedicaVO();
			vo.setIdx(++index);
			vo.setAtdSeq(internacao.getAtdSeq());
			vo.setDthrInicio(dtInicio.getTime());
			vo.setCodPaciente(codPaciente);
			vo.setAtdPac(getAghuFacade().recuperarAtendimentoPaciente(internacao.getAtdSeq()));				
			if (dtAux.after(dtFim)){
				vo.setDthrFim(dtFim.getTime());
			}else{
				vo.setDthrFim(dtAux.getTime());				
			}
			if (!buscaRelSumarioPrescricao(vo,true).isEmpty()){
				listQuinzenaPrescricaoMedica.add(vo);
			}			
			dtInicio.setTime(dtAux.getTime());			
			dtInicio.add(Calendar.DATE, 1);
		}
		
		return listQuinzenaPrescricaoMedica;
	}		
	
	public List<RelSumarioPrescricaoVO> buscaRelSumarioPrescricao(SumarioPrescricaoMedicaVO vo, boolean limitaResultado){		
		List<RelSumarioPrescricaoVO> result = getPrescricaoMedicaFacade().pesquisaGrupoDescricaoStatus(vo,limitaResultado);
		List<RelSumarioPrescricaoVO> newList = new ArrayList<RelSumarioPrescricaoVO>();
		
		Date inicio = vo.getDthrInicio();
		Date fim = vo.getDthrFim();
		List<Integer> dias = new ArrayList<Integer>();
		while (!DateUtil.truncaData(inicio).after(DateUtil.truncaData(fim))){
			dias.add(Integer.valueOf(DateUtil.dataToString(inicio, "dd")));
			inicio = DateUtil.adicionaDias(inicio, 1);
		}

		RelSumarioPrescricaoVO aux = null;
		RelSumarioPrescricaoVO sumario = null;
		for (RelSumarioPrescricaoVO obj: result){			
			Calendar d = Calendar.getInstance();
			d.setTime(obj.getData());
			
			if (aux != null && aux.getDescricao() != null && aux.getDescricao().equals(obj.getDescricao())) {
				if (aux.equals(obj) && obj.getValor().equals(DominioValorDataItemSumario.S.toString())) {
					String status = sumario.getStatus()[dias.indexOf(d.get(Calendar.DATE))] + " " + DominioValorDataItemSumario.S.toString();
					sumario.getStatus()[dias.indexOf(d.get(Calendar.DATE))] = status;
				} else {
					sumario.getStatus()[dias.indexOf(d.get(Calendar.DATE))] = obj.getValor();		
				}				
			} else {
				obj.getStatus()[dias.indexOf(d.get(Calendar.DATE))] = obj.getValor();				
				newList.add(obj);
				sumario = obj;				
			}	
			aux = obj;
		}		
		return newList;	
	}
	
	/**
	 * Método que carrega a lista quinzenal de seleção para o relatório
	 * de prescrição enfermagem
	 * @throws ApplicationBusinessException
	 */
	public List<SumarioPrescricaoEnfermagemVO> montaQuinzenaPrescricaoEnfermagem(InternacaoVO internacao, int codPaciente) throws ApplicationBusinessException{
		
		List<SumarioPrescricaoEnfermagemVO> listQuinzenaPrescricaoEnfermagem = new ArrayList<SumarioPrescricaoEnfermagemVO>();
		
		Calendar dtInicio = Calendar.getInstance(); 
		dtInicio.setTime(internacao.getDtReferencia()!=null?
				DateUtil.truncaData(internacao.getDtReferencia()):DateUtil.truncaData(internacao.getDthrInicio()));
		
		Calendar dtFim = Calendar.getInstance();
		if (internacao.getDthrFim()!=null){
			dtFim.setTime(DateUtil.truncaData(internacao.getDthrFim()));
		}		
		Calendar dtAux = Calendar.getInstance();
		Integer index = 0;
		while(!DateUtil.truncaData(dtInicio.getTime()).after(DateUtil.truncaData(dtFim.getTime()))) {
			
			dtAux.setTime(dtInicio.getTime());			
			dtAux.add(Calendar.DATE, 14);
			
			SumarioPrescricaoEnfermagemVO vo = new SumarioPrescricaoEnfermagemVO();
			vo.setIdx(++index);
			vo.setAtdSeq(internacao.getAtdSeq());
			vo.setDthrInicio(dtInicio.getTime());
			vo.setCodPaciente(codPaciente);
			vo.setAtdPac(getAghuFacade().recuperarAtendimentoPaciente(internacao.getAtdSeq()));				
			if (dtAux.after(dtFim)){
				vo.setDthrFim(dtFim.getTime());
			}else{
				vo.setDthrFim(dtAux.getTime());				
			}
			if (!buscaRelSumarioPrescricaoEnfermagem(vo,true).isEmpty()){
				listQuinzenaPrescricaoEnfermagem.add(vo);
			}			
			dtInicio.setTime(dtAux.getTime());			
			dtInicio.add(Calendar.DATE, 1);
		}
		
		return listQuinzenaPrescricaoEnfermagem;
	}		
	
	/**
	 * Método que carrega a lista de sumarios de alta com transferencias concluidas
	 * #16679 - Visualizar lista de sumarios de transferencia
	 * @throws ApplicationBusinessException
	 */	
		public List<AltaSumarioVO> carregarListaSumarioVO(Integer apaAtdSeq) throws ApplicationBusinessException{
						
			List<AltaSumarioVO> listaSumariosTransferencia = new ArrayList<AltaSumarioVO>();
			
			Long countTransf = getPrescricaoMedicaFacade().buscaAltaSumarioTransferenciaCount(apaAtdSeq);
			
			if (countTransf > 1){
				// Se countTransf > 1, significa que o atendimento tem mais de uma transferência e deverá ser apresentada uma lista contendo as
				// transferências para que o usuário selecione a que deseja viusualizar
				List<MpmAltaSumario> listaAltaSumarios	=  getPrescricaoMedicaFacade().obterTrfDestinoComAltaSumarioEPaciente(apaAtdSeq);
				
				for(int i =0; i< listaAltaSumarios.size(); i++){
					MpmAltaSumario altaSumario = listaAltaSumarios.get(i);
					AltaSumarioVO vo = new AltaSumarioVO();
					
					vo.setDataElaboracaoTransf(altaSumario.getDthrElaboracaoTransf());
					vo.setDestino(null);
					Integer count = 0;
					if (altaSumario.getMpmTrfDestino().getDescEquipe() != null){
						vo.setDestino(PARA_ + altaSumario.getMpmTrfDestino().getDescEquipe());
					}
				if (altaSumario.getMpmTrfDestino().getDescEquipe() != null){
						vo.setDestino(PARA_ + altaSumario.getMpmTrfDestino().getDescEquipe());
						count = count + 1;
					}
					if (altaSumario.getMpmTrfDestino().getDescEsp()!= null){
						if (count == 1){
							vo.setDestino(vo.getDestino() +"; " );
						}
						else {
							vo.setDestino(PARA_);
						}
						vo.setDestino(vo.getDestino() + altaSumario.getMpmTrfDestino().getDescEsp());
					}
					if (altaSumario.getMpmTrfDestino().getDescUnidade() != null){
						if (count == 1){
							vo.setDestino(vo.getDestino() +"; " );
						}
						else {
							vo.setDestino(PARA_);
						}
						vo.setDestino(vo.getDestino() + altaSumario.getMpmTrfDestino().getDescUnidade());
					}
					if (altaSumario.getMpmTrfDestino().getDescInstituicao() != null){
						if (count == 1){
							vo.setDestino(vo.getDestino() +"; " );
						}
						else {
							vo.setDestino(PARA_);
						}
						vo.setDestino(vo.getDestino() + altaSumario.getMpmTrfDestino().getDescInstituicao());
					}
					
					vo.setDestino(vo.getDestino().toUpperCase());
					vo.setId(new MpmAltaSumarioId(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp()));
					
				listaSumariosTransferencia.add(vo);
				
				}
			}
			return listaSumariosTransferencia;
		}

			
	public Boolean verificarSeInternacaoVariasTransferencias(Integer apaAtdSeq){
		Long countTransf = getPrescricaoMedicaFacade().buscaAltaSumarioTransferenciaCount(apaAtdSeq);
		if(countTransf != null && countTransf > 1){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	/**
	 * Método que busca o relatporio para que sejam carregados
	 * os dados quinzenais
	 * @param vo
	 * @param limitaResultado
	 * @return
	 */
	public List<RelSumarioPrescricaoEnfermagemVO> buscaRelSumarioPrescricaoEnfermagem(SumarioPrescricaoEnfermagemVO vo, boolean limitaResultado){		
		List<RelSumarioPrescricaoEnfermagemVO> result = getPrescricaoEnfermagemFacade().pesquisaGrupoDescricaoStatus(vo,limitaResultado);
		List<RelSumarioPrescricaoEnfermagemVO> newList = new ArrayList<RelSumarioPrescricaoEnfermagemVO>();
		
		Date inicio = vo.getDthrInicio();
		Date fim = vo.getDthrFim();

		List<Integer> dias = new ArrayList<Integer>();
		while (!DateUtil.truncaData(inicio).after(DateUtil.truncaData(fim))){
			dias.add(Integer.valueOf(DateUtil.dataToString(inicio, "dd")));
			inicio = DateUtil.adicionaDias(inicio, 1);
		}
		RelSumarioPrescricaoEnfermagemVO aux = null;
		RelSumarioPrescricaoEnfermagemVO sumario = null;
		for (RelSumarioPrescricaoEnfermagemVO obj: result){			
			Calendar d = Calendar.getInstance();
			d.setTime(obj.getData());
			
			if (aux != null && aux.getDescricao() != null && aux.getDescricao().equals(obj.getDescricao())) {
				if (aux.equals(obj) && obj.getValor().equals(DominioValorDataItemSumario.S.toString())) {
					String status = sumario.getStatus()[dias.indexOf(d.get(Calendar.DATE))] + " " + DominioValorDataItemSumario.S.toString();
					sumario.getStatus()[dias.indexOf(d.get(Calendar.DATE))] = status;
				} else {
					sumario.getStatus()[dias.indexOf(d.get(Calendar.DATE))] = obj.getValor();		
				}				
			} else {
				obj.getStatus()[dias.indexOf(d.get(Calendar.DATE))] = obj.getValor();				
				newList.add(obj);
				sumario = obj;				
			}	
			aux = obj;
		}		
		return newList;	
	}
	
	/*
	 * #4336 - Imprimir sumário da prescrição médica
	 * Modulo: Atualizar data de impressão de sumário
	 * Autor: Tiago Felini
	 * Data: 14/02/2010
	 */		
	public void atualizarDataImpSumario(Integer atdSeq, Date inicio, Date Fim, String enderecoHost) throws ApplicationBusinessException{	
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
		List<MpmPrescricaoMedica> lista = getPrescricaoMedicaFacade()
				.listarPrescricoesMedicasParaGerarSumarioDePrescricao(atdSeq,inicio, Fim);
		
		Calendar dataAtual = Calendar.getInstance();
		dataAtual.set(Calendar.SECOND, 0);
		dataAtual.set(Calendar.MILLISECOND, 0);
		dataAtual.set(Calendar.MINUTE, 0);
		dataAtual.set(Calendar.HOUR, 0);
		
		for (MpmPrescricaoMedica prescricaoMedica : lista){
			prescricaoMedica.setDataImpSumario(dataAtual.getTime());
			prescricaoMedica.setServidorAtualizada(servidorLogado);
			prescricaoMedica.setMachine(enderecoHost);
			getPrescricaoMedicaFacade().atualizarMpmPrescricaoMedicaDepreciado(prescricaoMedica);
		}		
	}
	
	/**
	 * @ORADB EPEP_ATLZ_PRCR_ENF
	 * Atualiza de impressão do sumário, servidor atualizador e o tipo de emissão
	 * @param atdSeq
	 * @param inicio
	 * @param Fim
	 * @throws ApplicationBusinessException  
	 */
	public void atualizarDataImpSumarioEnfermagem(Integer atdSeq, Date inicio, Date Fim) throws BaseException {		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		List<EpePrescricaoEnfermagem> lista = getPrescricaoEnfermagemFacade()
				.listarPrescricoesParaGerarSumarioDePrescricaoEnfermagem(atdSeq,inicio, Fim);
		
		Calendar dataAtual = Calendar.getInstance();
		dataAtual.set(Calendar.SECOND, 0);
		dataAtual.set(Calendar.MILLISECOND, 0);
		dataAtual.set(Calendar.MINUTE, 0);
		dataAtual.set(Calendar.HOUR, 0);
		
		IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade = this.getPrescricaoEnfermagemFacade();
		
		for (EpePrescricaoEnfermagem prescricaoEnfermagem: lista){
			EpePrescricaoEnfermagem prescricaoEnfermagemOld = getPrescricaoEnfermagemFacade().clonarPrescricaoEnfermagem(prescricaoEnfermagem);
			
			prescricaoEnfermagem.setDataImpSumario(dataAtual.getTime());
			prescricaoEnfermagem.setServidorAtualizada(servidorLogado);
			prescricaoEnfermagem.setTipoEmissaoSumario(DominioTipoEmissaoSumario.P);
			
			prescricaoEnfermagemFacade.atualizarPrescricao(prescricaoEnfermagemOld, prescricaoEnfermagem, true);
		}		
	}
	
	public boolean habilitarBotaoNascimentoComAtendimento(Integer codPaciente, Integer numConsulta, Short gsoSeqp) {
		
		if(codPaciente == null && numConsulta == null){
			return false;
		}
		
		//Boolean retorno = Boolean.TRUE;
		List<AghAtendimentos> atendimentos = getAghuFacade().pesquisarAghAtendimentosPorPacienteEConsulta(codPaciente, numConsulta);
		if (atendimentos.isEmpty()) {
			return false;
		} else {
			for (AghAtendimentos atd : atendimentos) {
				List<McoRecemNascidos> rns = getPerinatologiaFacade().listarPorGestacao(codPaciente, atd.getGsoSeqp());				
				if (rns.isEmpty()) {
					return false;
				}
			}
		}
		
		return getInformacoesPerinataisON().habilitarBotaoNascimento(codPaciente, gsoSeqp);
	}	 
	
	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}

	/**
	 * Método que busca o status de alta/óbito para ser utilizado na
	 * apresentação dos ícones e chamada dos documentos/relatorios
	 * 
	 * @param internacao
	 * @param possuiCertificacaoEletronica
	 * @return
	 */
	protected void atualizarStatusSumarioInternacao(AinInternacao internacao,
			DominioSimNao possuiCertificacaoEletronica,
			InternacaoVO internacaoVO) {

		if (internacao == null || possuiCertificacaoEletronica == null
				|| internacaoVO == null) {
			throw new IllegalArgumentException("Argumentos inválidos!");
		}
		
		// Quando paciente protegido então não apresenta o relatório
		if (DominioSimNao.S.equals(internacao.getPaciente()
				.getIndPacProtegido())) {
			internacaoVO.setStatusAltaObito(this
					.getStatusRelatorioAltaObito(null));
			return;
		}
		
		DominioSimNao isObito = this.getPrescricaoMedicaFacade()
				.verificarAltaSumarioObito(internacao.getAtendimento());

		AghVersaoDocumento aghVersaoDocumento;

		if (DominioSimNao.S.equals(possuiCertificacaoEletronica)
				&& isObito != null) {
			aghVersaoDocumento = this
					.getCertificacaoDigitalFacade()
					.verificarSituacaoDocumentoPorPaciente(
							internacao.getAtendimento().getSeq(),
							DominioSimNao.S.equals(isObito) ? DominioTipoDocumento.SO
									: DominioTipoDocumento.SA);
			if (aghVersaoDocumento != null) {
				if (DominioSituacaoVersaoDocumento.A.equals(aghVersaoDocumento
						.getSituacao())) {
					internacaoVO
							.setStatusAltaObito(StatusAltaObito.APRESENTA_DOCUMENTO_ASSINADO);
					internacaoVO.setSeqVersaoDocumento(aghVersaoDocumento
							.getSeq());
				} else if (DominioSituacaoVersaoDocumento.P
						.equals(aghVersaoDocumento.getSituacao())) {
					internacaoVO
							.setStatusAltaObito(StatusAltaObito.APRESENTA_DOCUMENTO_PENDENTE);
					internacaoVO.setSeqVersaoDocumento(aghVersaoDocumento
							.getSeq());
				} else {
					internacaoVO.setStatusAltaObito(this
							.getStatusRelatorioAltaObito(isObito));
				}
			} else {
				internacaoVO.setStatusAltaObito(this
						.getStatusRelatorioAltaObito(isObito));
			}
		} else {
			internacaoVO.setStatusAltaObito(this
					.getStatusRelatorioAltaObito(isObito));
		}

	}
	
	protected StatusAltaObito getStatusRelatorioAltaObito(DominioSimNao isObito) {
		if (DominioSimNao.S.equals(isObito)) {
			return StatusAltaObito.APRESENTA_RELATORIO_SUMARIO_OBITO;
		} else if (DominioSimNao.N.equals(isObito)) {
			return StatusAltaObito.APRESENTA_RELATORIO_SUMARIO_ALTA;
		} else {
			return StatusAltaObito.NAO_APRESENTA_DOCUMENTO_ALTA_OBITO;
		}
	}
	
	/**
	 * Retorna se o botão parto no nodo internação do POL deve ficar habilitado
	 * @param codigoPaciente
	 * @param numeroConsulta
	 * @return
	 */
	public Boolean habilitarBotaoPartoComAtendimento(Integer codigoPaciente, Integer numeroConsulta, Short gsoSeqp) {
		if (this.getAghuFacade().pesquisarAghAtendimentosPorPacienteEConsulta(codigoPaciente, numeroConsulta).isEmpty()) {
			return false;
		}
		
		return getInformacoesPerinataisON().habilitarBotaoParto(codigoPaciente, gsoSeqp);
	}	
	
	private InformacoesPerinataisON getInformacoesPerinataisON(){
		return informacoesPerinataisON;
	}
	
	/**
	 * @ORADB EPEC_BUSCA_UNID_INT
	 * @param apaAtdSeq
	 * @param apaSeq
	 * @return
	 */
	public String obterUnidadeInternacao(Integer apaAtdSeq, Integer apaSeq) {
		StringBuilder vDataSigla = new StringBuilder();
		List<EpeUnidPacAtendimento> result = getPrescricaoEnfermagemFacade().obterEpeUnidPacAtendimentoPorApaAtdSeqApaSeq(apaAtdSeq, apaSeq);
		
		for (EpeUnidPacAtendimento upa : result) {
			if (upa.getUnidadeFuncional() != null) {
				if (StringUtils.isEmpty(vDataSigla.toString())) {
					vDataSigla.append(' ').append(obterDataSigla(upa));
				} else {
					vDataSigla.append(" - ").append(obterDataSigla(upa));
				}
			}
		}		
		return vDataSigla.toString();
	}
	
	private String obterDataSigla(EpeUnidPacAtendimento upa) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(upa.getUnidadeFuncional().getSigla())) {
			sb.append(upa.getUnidadeFuncional().getSigla());
		} else {
			sb.append(upa.getUnidadeFuncional().getAndar())
			  .append(' ')
			  .append(upa.getUnidadeFuncional().getIndAla());
		}
 		return sb.append(' ')
				 .append(DateUtil.dataToString(upa.getId().getDthrIngresso(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
				 .toString();
	}
	
	protected IControlePacienteFacade getControlePacienteFacade() {
		return this.controlePacienteFacade;
	}

	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade(){
		return certificacaoDigitalFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade(){
		return prescricaoEnfermagemFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
