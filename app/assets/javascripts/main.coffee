
setupTableButton = ->
  $(".complete-btn").click ->
    id = $(this).attr("data-id")
    $.ajax(
      url: "/task/" + id
      type: "post"
      data:
        completed: "true"
    ).done( -> reload())
  $(".delete-btn").click ->
    id = $(this).attr("data-id")
    $.ajax(
      url: "/task/" + id
      type: "delete"
    ).done( -> reload())

reload = ->
  url = if $("#show-only-not-done-check").prop("checked") then "/task?onlyNotYet=true" else "/task"
  $.getJSON(url).done (data) ->
    $("#task-table-body tr").remove()

    data.result.forEach (r) ->
      tr = $("<tr>")
      .append($("<td>#{ if r.completed then "finished" else "not yet"}</td>"))
      .append($("<td>#{r.name}</td>"))
      .append($("<td>#{moment(r.dueDate, "YYYYMMDDHHmmss").format("YYYY/MM/DD (ddd) HH:mm")}</td>"))
      .append($("""<td>
        <button data-id="#{r.id}" class="btn btn-primary btn-sm complete-btn">Complete</button>
        <button data-id="#{r.id}" class="btn btn-danger btn-sm delete-btn">Delete</button>
        </td>"""))
      $("#task-table-body").append(tr)
    setupTableButton()

addTaskClicked = ->
  $("#warn-message").hide()
  $.ajax(
    url: "/task"
    type: "put"
    data:
      name: $("#task-name").val()
      dueDate: $("#due-date").data("DateTimePicker").date().format("YYYYMMDDHHmmss")
  ).done( -> reload())
  .fail( -> $("#warn-message").show())


$ ->
  moment.locale("ja")

  # setup HTML controls
  $("#add-task-button").click addTaskClicked
  $("#due-date").datetimepicker(
    locale: 'ja'
    format: 'YYYY/MM/DD HH:mm'
    allowInputToggle: true
  )
  $("#show-only-not-done-check").change ->
    reload()


  # display first data
  reload()

