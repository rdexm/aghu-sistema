package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MpmAltaEstadoPaciente;
import br.gov.mec.aghu.model.MpmAltaEvolucao;
import br.gov.mec.aghu.model.MpmAltaImpDiagnostica;
import br.gov.mec.aghu.model.MpmAltaPlano;
import br.gov.mec.aghu.model.MpmAltaRecomendacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmAltaTriagem;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.RelatorioSumarioAltaAtendEmergenciaEvolucaoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.RelatorioSumarioAltaAtendEmergenciaPOLVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioSumarioAltaAtendEmergenciaPOLON extends BaseBusiness {


@EJB
private ImprimeAltaAmbulatorialPolRN imprimeAltaAmbulatorialPolRN;

@EJB
private RelatorioSumarioAltaAtendEmergenciaPOLRN relatorioSumarioAltaAtendEmergenciaPOLRN;

@EJB
private ImprimeAltaAmbulatorialPolON imprimeAltaAmbulatorialPolON;

private static final Log LOG = LogFactory.getLog(RelatorioSumarioAltaAtendEmergenciaPOLON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@EJB
private IInternacaoFacade internacaoFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

	private static final long serialVersionUID = 5818797491989619250L;
	
		
	public Boolean verificarDadosRelatorioSumarioAltaAtendEmergencia(Integer atdSeq) {
		Boolean retorno = Boolean.FALSE;
		RelatorioSumarioAltaAtendEmergenciaPOLVO vo = new RelatorioSumarioAltaAtendEmergenciaPOLVO();
		vo.setAsuApaAtdSeq(atdSeq);
		preencherCamposChave(vo);
		MpmAltaSumarioId idAltaSumario = new MpmAltaSumarioId(vo.getAsuApaAtdSeq(), vo.getAsuApaSeq(), vo.getAsuSeqp());
		Long count = getPrescricaoMedicaFacade().pesquisarAltaTriagemPeloMpmAltaSumarioIdCount(idAltaSumario); //C1 count
		if(count != null && count > 0){
			retorno = Boolean.TRUE;
		}		
		return retorno;
	}
	
	public List<RelatorioSumarioAltaAtendEmergenciaPOLVO> recuperarRelatorioSumarioAltaAtendEmergenciaPOLVO(Integer atdSeq) throws ApplicationBusinessException {
		String pRelatorio = "S";
		RelatorioSumarioAltaAtendEmergenciaPOLVO vo = new RelatorioSumarioAltaAtendEmergenciaPOLVO();
		vo.setAsuApaAtdSeq(atdSeq);
		List<RelatorioSumarioAltaAtendEmergenciaEvolucaoPOLVO> evolucao = new ArrayList<RelatorioSumarioAltaAtendEmergenciaEvolucaoPOLVO>();
		evolucao.add(new RelatorioSumarioAltaAtendEmergenciaEvolucaoPOLVO());
		vo.setEvolucao(evolucao);
		preencherCamposChave(vo);
		MpmAltaSumarioId idAltaSumario = new MpmAltaSumarioId(vo.getAsuApaAtdSeq(), vo.getAsuApaSeq(), vo.getAsuSeqp());
		List<MpmAltaTriagem> altaTriagens = getPrescricaoMedicaFacade().pesquisarAltaTriagemPeloMpmAltaSumarioId(idAltaSumario); //C1
		List<RelatorioSumarioAltaAtendEmergenciaPOLVO> retorno = new ArrayList<RelatorioSumarioAltaAtendEmergenciaPOLVO>();
		if(altaTriagens != null && ! altaTriagens.isEmpty()){
			MpmAltaTriagem altaTriagem = altaTriagens.get(0);
			
			vo.setIndNoConsultorio(Boolean.TRUE.equals(altaTriagem.getAltaSumario().getNoConsultorio()));//nvl(asu.ind_no_consultorio, 'N') 
			vo.setTituloRelatorio(getRelatorioSumarioAltaAtendEmergenciaPOLRN().montarTituloSumarioAltaAtendEmergencia(idAltaSumario));
			preencherPosto(vo, altaTriagem.getAltaSumario().getPaciente().getCodigo());
			preencherPrevia(vo, altaTriagem.getAltaSumario().getTipo());
			
			preecheQueixaPrincipal(vo, altaTriagem);
			preencherIdentificacao(vo, altaTriagem);
			preencherTriagem(pRelatorio, vo, idAltaSumario, altaTriagem);
			preecherAtendimento(pRelatorio, vo, altaTriagem, idAltaSumario);
			preencherEvolucao(vo, altaTriagem);
			retorno.add(vo);
		}
		
		return retorno;
	}

	private void preencherPrevia(RelatorioSumarioAltaAtendEmergenciaPOLVO vo, DominioIndTipoAltaSumarios tipo) {
		if(DominioIndTipoAltaSumarios.ANT.equals(tipo)){
			vo.setPrevia("Este não é um documento definitivo. Não deve ser arquivado.");
		}else{
			vo.setPrevia(null);
		}
	}

	protected void preencherPosto(RelatorioSumarioAltaAtendEmergenciaPOLVO vo, Integer pacCodigo) {
		StringBuilder posto = new StringBuilder(70);
		posto.append("A continuidade de seu atendimento deverá ser feita no posto de saúde ");
		if(getInternacaoFacade().obterPostoSaudePaciente(pacCodigo) != null){
			posto.append(getInternacaoFacade().obterPostoSaudePaciente(pacCodigo)).append('.');
		}else{
			posto.append("mais perto de sua residência").append('.');
		}	
		vo.setPosto(posto.toString());
	}

	protected void preecheQueixaPrincipal(
			RelatorioSumarioAltaAtendEmergenciaPOLVO vo,
			MpmAltaTriagem altaTriagem) {
		if(".".equals(altaTriagem.getDescQueixa())){
			vo.setQueixaPrincipal("S");
		}else{
			vo.setQueixaPrincipal("N");
		}
	}

	protected void preecherAtendimento(String pRelatorio, RelatorioSumarioAltaAtendEmergenciaPOLVO vo,
			MpmAltaTriagem altaTriagem, MpmAltaSumarioId idAltaSumario) throws ApplicationBusinessException {
		//	Atendimento = 16 = 29 
		Boolean pNoConsultorio = Boolean.TRUE.equals(altaTriagem.getAltaSumario().getNoConsultorio());// == null ? Boolean.FALSE : Boolean.TRUE;
		//30 
		if(pNoConsultorio){
			vo.setAssinatura(getImprimeAltaAmbulatorialPolRN().buscaAssinaturaMedicoCrm(
					altaTriagem.getAltaSumario().getServidorValida().getId().getMatricula(),
					altaTriagem.getAltaSumario().getServidorValida().getId().getVinCodigo()));
		}

		//C2/RN4
		LinhaReportVO linha = new LinhaReportVO();
		String descAtendimento = getRelatorioSumarioAltaAtendEmergenciaPOLRN().buscarDescricaoAtendimento(idAltaSumario, pNoConsultorio, pRelatorio);
		if (descAtendimento != null){
			linha.setTexto1(descAtendimento);
			linha.setTexto2(vo.getAssinatura());
			linha.setBool(vo.getIndNoConsultorio());
			vo.setDescAtendimento(Arrays.asList(linha));
		}else{
			vo.setDescAtendimento(null);
		}
	
	}

	protected void preencherTriagem(String pRelatorio,
			RelatorioSumarioAltaAtendEmergenciaPOLVO vo,
			MpmAltaSumarioId idAltaSumario, MpmAltaTriagem altaTriagem) {
		//Triagem = 15 = 31
		if ("N".equals(vo.getQueixaPrincipal())) {
			//C3/RN5
			LinhaReportVO linha = new LinhaReportVO();
			linha.setTexto1(getRelatorioSumarioAltaAtendEmergenciaPOLRN().buscarDescricaoTriagem(idAltaSumario, pRelatorio));
			vo.setDescTriagem(Arrays.asList(linha));
		}
	}

	protected void preencherIdentificacao(
			RelatorioSumarioAltaAtendEmergenciaPOLVO vo,
			MpmAltaTriagem altaTriagem) {
		//identificação
		vo.setNome(altaTriagem.getAltaSumario().getNome()); //5
		vo.setProntuario(CoreUtil.formataProntuarioRelatorio(altaTriagem.getAltaSumario().getProntuario())); //6
		vo.setDtNascimento(altaTriagem.getAltaSumario().getDtNascimento()); //7
		vo.setIdade(this.getImprimeAltaAmbulatorialPolON().criaIdadeExtenso(altaTriagem.getAltaSumario().getIdadeAnos(),altaTriagem.getAltaSumario().getIdadeMeses(),altaTriagem.getAltaSumario().getIdadeDias()));//8
		vo.setSexo(altaTriagem.getAltaSumario().getSexo() != null ? altaTriagem.getAltaSumario().getSexo().getDescricao() : null);//9
		vo.setDescPlanoConvenio(altaTriagem.getAltaSumario().getDescPlanoConvenio());//10
		vo.setMatriculaConvenio(altaTriagem.getAltaSumario().getMatriculaConvenio());//11
		vo.setEndereco(altaTriagem.getAltaSumario().getEndereco());//12
		vo.setDthrTriagem(altaTriagem.getAltaSumario().getDthrTriagem()); //13
		vo.setDthrAlta(altaTriagem.getAltaSumario().getDthrAlta()); //14
	}

	protected void preencherEvolucao(RelatorioSumarioAltaAtendEmergenciaPOLVO vo, MpmAltaTriagem altaTriagem) throws ApplicationBusinessException {
		RelatorioSumarioAltaAtendEmergenciaEvolucaoPOLVO evolucao = new RelatorioSumarioAltaAtendEmergenciaEvolucaoPOLVO();
		
		evolucao.setAssinatura((getImprimeAltaAmbulatorialPolRN().buscaAssinaturaMedicoCrm(
				altaTriagem.getAltaSumario().getServidorValida().getId().getMatricula(),
				altaTriagem.getAltaSumario().getServidorValida().getId().getVinCodigo())));
		evolucao.setIndNoConsultorio(vo.getIndNoConsultorio());

		List<LinhaReportVO> descricaoMedicamentos = preencherDescricaoMedicamento(vo);
		List<LinhaReportVO> consultorias = preencherConsultorias(vo);
		List<LinhaReportVO> descExame = preencherExame(vo);
		List<LinhaReportVO> descEvolucao = preencherDescEvolucao(vo);
		List<LinhaReportVO> descDiagnostica = preencherDescDiagnostica(vo);
		List<LinhaReportVO> estado = preencherEstado(vo);
		List<LinhaReportVO> plano = preencherPlano(vo);
		List<LinhaReportVO> recomendacoes = preencherRecomendacoes(vo);
		List<LinhaReportVO> descricaoQuantidades = preencherDescricaoQuantidade(vo);

		evolucao.setDescMedicamento(descricaoMedicamentos);
		evolucao.setConsultorias(consultorias);
		evolucao.setDescExame(descExame);
		evolucao.setDescEvolucao(descEvolucao);
		evolucao.setDescDiagnostica(descDiagnostica);
		evolucao.setEstado(estado);
		evolucao.setPlano(plano);
		evolucao.setRecomendacao(recomendacoes);
		evolucao.setDescricaoQuantidade(descricaoQuantidades);
		vo.setEvolucao(Arrays.asList(evolucao));
	}

	private List<LinhaReportVO> preencherConsultorias(
			RelatorioSumarioAltaAtendEmergenciaPOLVO vo) throws ApplicationBusinessException {
		LinhaReportVO linha = new LinhaReportVO();
		MpmAltaSumarioId mpmAltaSumarioId = new MpmAltaSumarioId(vo.getAsuApaAtdSeq(), vo.getAsuApaSeq(), vo.getAsuSeqp());
		linha.setTexto1(getRelatorioSumarioAltaAtendEmergenciaPOLRN().buscarConsultoria(mpmAltaSumarioId, "S"));
		return Arrays.asList(linha);
	}

	private List<LinhaReportVO> preencherDescricaoMedicamento(
			RelatorioSumarioAltaAtendEmergenciaPOLVO vo) throws ApplicationBusinessException {
		MpmAltaSumarioId mpmAltaSumarioId = new MpmAltaSumarioId(vo.getAsuApaAtdSeq(), vo.getAsuApaSeq(), vo.getAsuSeqp());
		List<String> descricaoMedicamento = getRelatorioSumarioAltaAtendEmergenciaPOLRN().buscarDescricaoMedicamento(mpmAltaSumarioId, "S");
		List<LinhaReportVO> descMedicamento = new ArrayList<LinhaReportVO>();
		
		for (String medicamento : descricaoMedicamento) {
			LinhaReportVO linha = new LinhaReportVO();
			linha.setTexto1(medicamento);
			descMedicamento.add(linha);
		}
		return descMedicamento;
	}

	private List<LinhaReportVO> preencherExame(RelatorioSumarioAltaAtendEmergenciaPOLVO vo) {
		LinhaReportVO descEvolucao = new LinhaReportVO();
		descEvolucao.setTexto1(getRelatorioSumarioAltaAtendEmergenciaPOLRN().montarExamesRealizados(vo.getAsuApaAtdSeq(), vo.getAsuApaSeq(), vo.getAsuSeqp(), Boolean.TRUE));
		return Arrays.asList(descEvolucao);
	}

	private List<LinhaReportVO> preencherDescEvolucao(RelatorioSumarioAltaAtendEmergenciaPOLVO vo) {
		LinhaReportVO descEvolucao = new LinhaReportVO();
		
		MpmAltaEvolucao mpmAltaEvolucao = getPrescricaoMedicaFacade().obterMpmAltaEvolucao(vo.getAsuApaAtdSeq(), vo.getAsuApaSeq(), vo.getAsuSeqp());
		if(mpmAltaEvolucao != null && mpmAltaEvolucao.getDescricao() != null){
			descEvolucao.setTexto1(mpmAltaEvolucao.getDescricao());
		}else{
			descEvolucao.setTexto1(null);
		}
		
		return Arrays.asList(descEvolucao);
	}

	private List<LinhaReportVO> preencherDescDiagnostica(RelatorioSumarioAltaAtendEmergenciaPOLVO vo) {
		List<MpmAltaImpDiagnostica> mpmAltasImpDiagnostica = getPrescricaoMedicaFacade().listarAltaImpDiagnosticaPorIdSemSeqp(vo.getAsuApaAtdSeq(), vo.getAsuApaSeq(), vo.getAsuSeqp());
		List<LinhaReportVO> descDiagnostica = new ArrayList<LinhaReportVO>();
		for (MpmAltaImpDiagnostica mpmAltaImpDiagnostica : mpmAltasImpDiagnostica) {
			LinhaReportVO recomendacao = new LinhaReportVO();
			String retorno;
			retorno = mpmAltaImpDiagnostica.getCidCodigo().concat("-").concat(mpmAltaImpDiagnostica.getDescDiagnostico());
			if(mpmAltaImpDiagnostica.getComplemento() != null){
				retorno = retorno.concat("-").concat(mpmAltaImpDiagnostica.getComplemento());
			}
			recomendacao.setTexto1(retorno);
			descDiagnostica.add(recomendacao);
		}
		
		return descDiagnostica;
	}

	private List<LinhaReportVO> preencherEstado(RelatorioSumarioAltaAtendEmergenciaPOLVO vo) {
		LinhaReportVO estado = new LinhaReportVO();
		
		MpmAltaSumarioId mpmAltaSumarioId = new MpmAltaSumarioId(vo.getAsuApaAtdSeq(), vo.getAsuApaSeq(), vo.getAsuSeqp());
		MpmAltaEstadoPaciente mpmAltaEstadoPaciente = getPrescricaoMedicaFacade().obterMpmAltaEstadoPacientePorChavePrimaria(mpmAltaSumarioId);
		
		if (mpmAltaEstadoPaciente != null){
			if(mpmAltaEstadoPaciente.getDescricaoSituacaoSaida() == null){
				estado.setTexto1(mpmAltaEstadoPaciente.getEstadoPaciente().getDescricao());
			}else{
				estado.setTexto1(mpmAltaEstadoPaciente.getDescricaoSituacaoSaida());
			}
		}else{
			estado.setTexto1(null);
		}
		
		return Arrays.asList(estado);
	}

	private List<LinhaReportVO> preencherPlano(RelatorioSumarioAltaAtendEmergenciaPOLVO vo) {
		MpmAltaSumarioId altaSumarioId = new MpmAltaSumarioId(vo.getAsuApaAtdSeq(), vo.getAsuApaSeq(), vo.getAsuSeqp());
		MpmAltaPlano mpmAltaPlano = getPrescricaoMedicaFacade().obterMpmAltaPlanoObterPorChavePrimaria(altaSumarioId);
		String retorno;
		LinhaReportVO altaPlano = new LinhaReportVO();

		if(mpmAltaPlano != null){
			retorno = mpmAltaPlano.getDescPlanoPosAlta();
			if(mpmAltaPlano.getComplPlanoPosAlta() != null){
				retorno = retorno.concat("-").concat(mpmAltaPlano.getComplPlanoPosAlta());
			}
		}else{
			retorno = null;
		}
		
		altaPlano.setTexto1(retorno);
		return Arrays.asList(altaPlano);
	}

	private List<LinhaReportVO> preencherRecomendacoes(RelatorioSumarioAltaAtendEmergenciaPOLVO vo)	throws ApplicationBusinessException {
		List<MpmAltaRecomendacao> mpmAltaRecomendacoes = getPrescricaoMedicaFacade().obterMpmAltaRecomendacoes(vo.getAsuApaAtdSeq(), vo.getAsuApaSeq(), vo.getAsuSeqp(), null);
		List<LinhaReportVO> recomendacoes = new ArrayList<LinhaReportVO>();
		for (MpmAltaRecomendacao mpmAltaRecomendacao : mpmAltaRecomendacoes) {
			LinhaReportVO recomendacao = new LinhaReportVO();
			recomendacao.setTexto1(mpmAltaRecomendacao.getDescricao());
			recomendacoes.add(recomendacao);
		}
		return recomendacoes;
	}

	private List<LinhaReportVO> preencherDescricaoQuantidade(RelatorioSumarioAltaAtendEmergenciaPOLVO vo) {
		List<MamItemReceituario> itensReceituario = getPrescricaoMedicaFacade().listarItemReceituarioPorAltaSumario(vo.getAsuApaAtdSeq(), vo.getAsuApaSeq(), vo.getAsuSeqp());
		List<LinhaReportVO> descricaoQuantidades = new ArrayList<LinhaReportVO>();
		for (MamItemReceituario item : itensReceituario) {
			LinhaReportVO linha = new LinhaReportVO();
			linha.setTexto1(item.getDescricao()+ "    "+ (item.getQuantidade() == null ? " " : item.getQuantidade()));
			descricaoQuantidades.add(linha);
		}
		return descricaoQuantidades;
	}

	protected void preencherCamposChave(RelatorioSumarioAltaAtendEmergenciaPOLVO vo) {
		List<MpmAltaSumario> listAlaSumario = getPrescricaoMedicaFacade().pesquisarAltaSumariosConcluidoAltaEObitoPorAtdSeq(vo.getAsuApaAtdSeq());
		if(listAlaSumario != null && listAlaSumario.get(0) != null){
			MpmAltaSumario mpmAltaSumario = listAlaSumario.get(0);
			vo.setAsuApaSeq(mpmAltaSumario.getId().getApaSeq());
			vo.setAsuSeqp(mpmAltaSumario.getId().getSeqp());
		}
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade(){
		return registroColaboradorFacade;
	}
	
	protected ImprimeAltaAmbulatorialPolON getImprimeAltaAmbulatorialPolON() {
		return imprimeAltaAmbulatorialPolON;
	}
	
	protected RelatorioSumarioAltaAtendEmergenciaPOLRN getRelatorioSumarioAltaAtendEmergenciaPOLRN() {
		return relatorioSumarioAltaAtendEmergenciaPOLRN;
	}
	
	protected ImprimeAltaAmbulatorialPolRN getImprimeAltaAmbulatorialPolRN() {
		return imprimeAltaAmbulatorialPolRN;
	}
	
	protected IInternacaoFacade getInternacaoFacade(){
		return internacaoFacade;
	}
}
